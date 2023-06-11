package net.permutated.pylons.tile;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExpulsionPylonTile extends AbstractPylonTile {

    private List<ResourceKey<Level>> allowedDimensions = null;

    public ExpulsionPylonTile(BlockPos pos, BlockState state) {
        super(ModRegistry.EXPULSION_PYLON_TILE.get(), pos, state);
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
                    temp.add(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(key)));
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
                CompoundTag tag = stack.getTagElement(Pylons.MODID);
                if (tag != null && tag.hasUUID(Constants.NBT.UUID)) {
                    allowed.add(tag.getUUID(Constants.NBT.UUID));
                }
            }
        }
        return allowed;
    }

    private void doRespawn(MinecraftServer server, ServerPlayer player) {
        BlockPos respawnPosition = player.getRespawnPosition();
        float respawnAngle = player.getRespawnAngle();
        boolean flag = player.isRespawnForced();

        ServerLevel respawnLevel = server.getLevel(player.getRespawnDimension());

        Optional<Vec3> optional;
        if (respawnLevel != null && respawnPosition != null) {
            optional = Player.findRespawnPositionAndUseSpawnBlock(respawnLevel, respawnPosition, respawnAngle, flag, true);
        } else {
            optional = Optional.empty();
        }

        ServerLevel actualLevel = respawnLevel != null && optional.isPresent() ? respawnLevel : server.overworld();
        ServerPlayer dummyPlayer = new ServerPlayer(server, actualLevel, player.getGameProfile());

        if (optional.isPresent()) {
            BlockState blockstate = actualLevel.getBlockState(respawnPosition);
            boolean isAnchor = blockstate.is(Blocks.RESPAWN_ANCHOR);
            Vec3 spawnPos = optional.get();
            float actualAngle;
            if (!blockstate.is(BlockTags.BEDS) && !isAnchor) {
                actualAngle = respawnAngle;
            } else {
                Vec3 vector3d = Vec3.atBottomCenterOf(respawnPosition).subtract(spawnPos).normalize();
                actualAngle = (float) Mth.wrapDegrees(Mth.atan2(vector3d.z, vector3d.x) * (180F / (float) Math.PI) - 90.0D);
            }

            dummyPlayer.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, actualAngle, 0.0F);

            // player has a spawn position, is the spawn position in the pylon's work area?
            var spawnPosition = Vec3.atBottomCenterOf(dummyPlayer.blockPosition());
            if (getBoundingBox(actualLevel).contains(spawnPosition)) {
                return;
            }
        }

        while (!actualLevel.noCollision(dummyPlayer) && dummyPlayer.getY() < 256.0D) {
            dummyPlayer.setPos(dummyPlayer.getX(), dummyPlayer.getY() + 1.0D, dummyPlayer.getZ());
        }

        player.teleportTo(actualLevel, dummyPlayer.getX(), dummyPlayer.getY(), dummyPlayer.getZ(), dummyPlayer.getYRot(), dummyPlayer.getXRot());
        player.sendSystemMessage(Component.translatable(TranslationKey.chat("expelled"), getOwnerName()).withStyle(ChatFormatting.RED));
    }
}
