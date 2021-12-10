package net.permutated.pylons.tile;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
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
    protected boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof PlayerFilterCard;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && canTick(10) && owner != null && isAllowedDimension()) {
            LevelChunk chunk = level.getChunkAt(worldPosition);

            var chunkPos = chunk.getPos();
            var aabb = new AABB(
                chunkPos.getMinBlockX(),
                level.getMinBuildHeight(),
                chunkPos.getMinBlockZ(),
                chunkPos.getMaxBlockX() + 1D,
                level.getMaxBuildHeight() + 1D,
                chunkPos.getMaxBlockZ() + 1D
            );

            var players = level.getEntitiesOfClass(ServerPlayer.class, aabb);

            List<UUID> allowed = allowedPlayers();

            MinecraftServer server = level.getServer();
            for (ServerPlayer player : players) {
                if (server != null
                    && !player.hasPermissions(2)
                    && !player.getUUID().equals(owner)
                    && !allowed.contains(player.getUUID())) {
                    doRespawn(server, player);
                }
            }
        }
    }

    @Override
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        super.updateContainer(packetBuffer);
        packetBuffer.writeBoolean(isAllowedDimension());
    }

    public boolean isAllowedDimension() {
        if (level != null) {
            if (allowedDimensions == null) {
                List<ResourceKey<Level>> temp = new ArrayList<>();
                List<? extends String> allowed = ConfigManager.COMMON.expulsionAllowedDimensions.get();
                for (String key : allowed) {
                    temp.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(key)));
                }
                allowedDimensions = ImmutableList.copyOf(temp);
            }
            return allowedDimensions.contains(level.dimension());
        }
        return false;
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

            // player has a spawn position, is this in the same chunk?
            if (sameChunk(actualLevel, dummyPlayer.blockPosition())) {
                return;
            }
        } else {
            int spawnRadius = server.getSpawnRadius(actualLevel);

            var bb = new BoundingBox(actualLevel.getSharedSpawnPos());
            var area = bb.inflatedBy(spawnRadius);

            // player does not have a spawn position, is this in the world spawn?
            // sameChunk(actualLevel, actualLevel.getSharedSpawnPos()) ||
            if (area.intersects(getBlockPos().getX(), getBlockPos().getZ(), getBlockPos().getX(), getBlockPos().getZ())) {
                return;
            }
        }

        while (!actualLevel.noCollision(dummyPlayer) && dummyPlayer.getY() < 256.0D) {
            dummyPlayer.setPos(dummyPlayer.getX(), dummyPlayer.getY() + 1.0D, dummyPlayer.getZ());
        }

        player.teleportTo(actualLevel, dummyPlayer.getX(), dummyPlayer.getY(), dummyPlayer.getZ(), dummyPlayer.getYRot(), dummyPlayer.getXRot());
        player.sendMessage(new TranslatableComponent(TranslationKey.chat("expelled"), getOwnerName()).withStyle(ChatFormatting.RED), player.getUUID());
    }

    private boolean sameChunk(Level world, BlockPos target) {
        if (level != null && level.dimension() == world.dimension()) {
            int thisX = worldPosition.getX() >> 4;
            int thisZ = worldPosition.getZ() >> 4;

            int thatX = target.getX() >> 4;
            int thatZ = target.getZ() >> 4;

            return thisX == thatX && thisZ == thatZ;
        }
        return false;
    }
}
