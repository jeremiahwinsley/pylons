package net.permutated.pylons.machines.expulsion;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.PlayerComponent;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.util.TranslationKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpulsionPylonTile extends AbstractPylonTile {

    @Nullable
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
    protected boolean isItemValid(ItemResource resource) {
        return resource.getItem() instanceof PlayerFilterCard;
    }

    protected AABB getBoundingBox(ServerLevel level) {

        var chunkPos = level.getChunkAt(worldPosition).getPos();
        var aabb = new AABB(
            chunkPos.getMinBlockX(),
            level.getMinY(),
            chunkPos.getMinBlockZ(),
            chunkPos.getMaxBlockX() + 1D,
            level.getMaxY() + 1D,
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
        if (level instanceof ServerLevel serverLevel && canTick(10) && isAllowedDimension() && isAllowedLocation()) {
            var aabb = getBoundingBox(serverLevel);
            var players = serverLevel.getEntitiesOfClass(ServerPlayer.class, aabb);

            if (!players.isEmpty()) {
                List<UUID> allowed = allowedPlayers();
                for (ServerPlayer player : players) {
                    if (!this.canAccess(player) && !allowed.contains(player.getUUID())) {
                        player.teleport(TeleportTransition.createDefault(player, TeleportTransition.DO_NOTHING));
                        player.sendSystemMessage(Component.translatable(TranslationKey.chat("expelled"), getOwnerName()).withStyle(ChatFormatting.RED));
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
                    temp.add(ResourceKey.create(Registries.DIMENSION, Identifier.parse(key)));
                }
                allowedDimensions = ImmutableList.copyOf(temp);
            }
            return allowedDimensions.contains(level.dimension());
        }
        return false;
    }

    public boolean isAllowedLocation() {
        if (level instanceof ServerLevel serverLevel) {
            int spawnRadius = serverLevel.getGameRules().get(GameRules.RESPAWN_RADIUS);
            int configRadius = ConfigManager.SERVER.expulsionWorldSpawnRadius.get();

            var bb = new AABB(serverLevel.getRespawnData().pos());
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
        for (int i = 0; i < itemStackHandler.size(); i++) {
            ItemResource resource = itemStackHandler.getResource(i);
            if (!resource.isEmpty() && resource.getItem() instanceof PlayerFilterCard) {
                PlayerComponent data = resource.get(ModRegistry.PLAYER_COMPONENT);
                if (data != null) {
                    allowed.add(data.uuid());
                }
            }
        }
        return allowed;
    }
}
