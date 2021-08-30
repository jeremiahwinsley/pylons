package net.permutated.pylons.tile;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.util.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class ExpulsionPylonTile extends AbstractPylonTile {

    private List<RegistryKey<World>> allowedDimensions = null;

    public ExpulsionPylonTile() {
        super(ModRegistry.EXPULSION_PYLON_TILE.get());
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof PlayerFilterCard;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && canTick(10) && owner != null && isAllowedDimension()) {
            Chunk chunk = level.getChunkAt(worldPosition);
            List<ServerPlayerEntity> players = Arrays.stream(chunk.getEntitySections())
                .map(multiMap -> multiMap.find(ServerPlayerEntity.class))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

            List<UUID> allowed = allowedPlayers();

            MinecraftServer server = level.getServer();
            for (ServerPlayerEntity player : players) {
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
    public void updateContainer(PacketBuffer packetBuffer) {
        super.updateContainer(packetBuffer);
        packetBuffer.writeBoolean(isAllowedDimension());
    }

    public boolean isAllowedDimension() {
        if (level != null) {
            if (allowedDimensions == null) {
                List<RegistryKey<World>> temp = new ArrayList<>();
                List<? extends String> allowed = ConfigManager.COMMON.expulsionAllowedDimensions.get();
                for (String key : allowed) {
                    temp.add(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(key)));
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
                CompoundNBT tag = stack.getTagElement(Pylons.MODID);
                if (tag != null && tag.hasUUID(Constants.NBT.UUID)) {
                    allowed.add(tag.getUUID(Constants.NBT.UUID));
                }
            }
        }
        return allowed;
    }

    private void doRespawn(MinecraftServer server, ServerPlayerEntity player) {
        BlockPos respawnPosition = player.getRespawnPosition();
        float respawnAngle = player.getRespawnAngle();
        boolean flag = player.isRespawnForced();

        ServerWorld respawnLevel = server.getLevel(player.getRespawnDimension());

        Optional<Vector3d> optional;
        if (respawnLevel != null && respawnPosition != null) {
            optional = PlayerEntity.findRespawnPositionAndUseSpawnBlock(respawnLevel, respawnPosition, respawnAngle, flag, true);
        } else {
            optional = Optional.empty();
        }

        ServerWorld actualLevel = respawnLevel != null && optional.isPresent() ? respawnLevel : server.overworld();
        PlayerInteractionManager manager = new PlayerInteractionManager(actualLevel);

        ServerPlayerEntity dummyPlayer = new ServerPlayerEntity(server, actualLevel, player.getGameProfile(), manager);

        if (optional.isPresent()) {
            // player has a spawn position, is this in the same chunk?
            if (sameChunk(actualLevel, dummyPlayer.blockPosition())) {
                return;
            }

            BlockState blockstate = actualLevel.getBlockState(respawnPosition);
            boolean isAnchor = blockstate.is(Blocks.RESPAWN_ANCHOR);
            Vector3d spawnPos = optional.get();
            float actualAngle;
            if (!blockstate.is(BlockTags.BEDS) && !isAnchor) {
                actualAngle = respawnAngle;
            } else {
                Vector3d vector3d = Vector3d.atBottomCenterOf(respawnPosition).subtract(spawnPos).normalize();
                actualAngle = (float) MathHelper.wrapDegrees(MathHelper.atan2(vector3d.z, vector3d.x) * (180F / (float) Math.PI) - 90.0D);
            }

            dummyPlayer.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, actualAngle, 0.0F);
        } else {
            // player does not have a spawn position, is this in the world spawn?
            if (sameChunk(actualLevel, actualLevel.getSharedSpawnPos())) {
                return;
            }
        }

        while (!actualLevel.noCollision(dummyPlayer) && dummyPlayer.getY() < 256.0D) {
            dummyPlayer.setPos(dummyPlayer.getX(), dummyPlayer.getY() + 1.0D, dummyPlayer.getZ());
        }

        player.teleportTo(actualLevel, dummyPlayer.getX(), dummyPlayer.getY(), dummyPlayer.getZ(), dummyPlayer.yRot, dummyPlayer.xRot);
    }

    private boolean sameChunk(World world, BlockPos target) {
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
