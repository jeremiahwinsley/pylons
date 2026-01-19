package net.permutated.pylons.machines.expulsion;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.PlayerComponent;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.util.TranslationKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpulsionPylonTile extends AbstractPylonTile {

    private List<ResourceKey<Level>> allowedDimensions = null;

    public ExpulsionPylonTile(BlockPos pos, BlockState state) {
        super(ModRegistry.EXPULSION_PYLON_TILE.get(), pos, state);
    }

    @Override
    public void removeChunkloads() {
        // nothing to do
    }

    @Override
    protected byte[] getRange() {
        return new byte[]{1, 3, 5};
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof PlayerFilterCard;
    }

    protected AABB getBoundingBox(ServerLevel level) {

        var chunkPos = level.getChunkAt(worldPosition).getPos();
        var aabb = new AABB(
            chunkPos.getMinBlockX(),
            level.getMinBuildHeight(),
            chunkPos.getMinBlockZ(),
            chunkPos.getMaxBlockX() + 1D,
            level.getMaxBuildHeight() + 1D,
            chunkPos.getMaxBlockZ() + 1D
        );

        var selected = range.get() - 1; // center chunk is already included

        var maxRadius = ConfigManager.SERVER.expulsionPylonMaxRadius.get();
        if (selected > maxRadius) {
            selected = maxRadius;
        }

        if (selected > 0) {
            return aabb.inflate(selected * 8D); // range is diameter, inflate is radius
        }
        return aabb;
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(10) && owner != null && isAllowedDimension() && isAllowedLocation()) {
            var aabb = getBoundingBox(serverLevel);
            var players = serverLevel.getEntitiesOfClass(ServerPlayer.class, aabb);

            if (!players.isEmpty()) {
                List<UUID> allowed = allowedPlayers();
                for (ServerPlayer player : players) {
                    if (!this.canAccess(player) && !allowed.contains(player.getUUID())) {
                        doRespawn(serverLevel.getServer(), player);
                    }
                }
            }
        }
    }

    @Override
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        super.updateContainer(packetBuffer);
        packetBuffer.writeBoolean(isAllowedDimension());
        packetBuffer.writeBoolean(isAllowedLocation());
    }

    public boolean isAllowedDimension() {
        if (level != null) {
            if (allowedDimensions == null) {
                List<ResourceKey<Level>> temp = new ArrayList<>();
                List<? extends String> allowed = ConfigManager.SERVER.expulsionAllowedDimensions.get();
                for (String key : allowed) {
                    temp.add(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(key)));
                }
                allowedDimensions = ImmutableList.copyOf(temp);
            }
            return allowedDimensions.contains(level.dimension());
        }
        return false;
    }

    public boolean isAllowedLocation() {
        if (level instanceof ServerLevel serverLevel) {
            int spawnRadius = serverLevel.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS);
            int configRadius = ConfigManager.SERVER.expulsionWorldSpawnRadius.get();

            var bb = new AABB(serverLevel.getSharedSpawnPos());
            var area = bb.inflate(Math.max(configRadius, spawnRadius));

            var workArea = getBoundingBox(serverLevel);
            return !area.intersects(workArea);
        } else {
            return false;
        }
    }

    /**
     * Iterates over Player Filters in the inventory and returns a list with all found UUIDs
     *
     * @return list of allowed UUIDs
     */
    private List<UUID> allowedPlayers() {
        List<UUID> allowed = new ArrayList<>();
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack stack = itemStackHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof PlayerFilterCard) {
                PlayerComponent data = stack.get(ModRegistry.PLAYER_COMPONENT);
                if (data != null) {
                    allowed.add(data.uuid());
                }
            }
        }
        return allowed;
    }

    private void doRespawn(MinecraftServer server, ServerPlayer player) {
        DimensionTransition transition = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);

        // player has a spawn position, is the spawn position in the pylon's work area?
        if (getBoundingBox(transition.newLevel()).contains(transition.pos())) {
            return;
        }

        player.teleportTo(transition.newLevel(), transition.pos().x(), transition.pos().y(), transition.pos().z(), transition.yRot(), transition.xRot());
        player.sendSystemMessage(Component.translatable(TranslationKey.chat("expelled"), getOwnerName()).withStyle(ChatFormatting.RED));
    }
}
