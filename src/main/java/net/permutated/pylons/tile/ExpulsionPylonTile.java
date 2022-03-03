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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

    private List<RegistryKey<World>> allowedDimensions = null;

    public ExpulsionPylonTile() {
        super(ModRegistry.EXPULSION_PYLON_TILE.get());
    }

    @Override
    protected byte[] getRange() {
        return new byte[]{1, 3, 5};
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof PlayerFilterCard;
    }

    protected AxisAlignedBB getBoundingBox(ServerWorld level) {

        ChunkPos chunkPos = level.getChunkAt(worldPosition).getPos();
        AxisAlignedBB aabb = new AxisAlignedBB(
            chunkPos.getMinBlockX(),
            0D,
            chunkPos.getMinBlockZ(),
            chunkPos.getMaxBlockX() + 1D,
            level.getMaxBuildHeight() + 1D,
            chunkPos.getMaxBlockZ() + 1D
        );

        int selected = range.get() - 1; // center chunk is already included
        if (selected > 0) {
            return aabb.inflate(selected * 8D); // range is diameter, inflate is radius
        }
        return aabb;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && canTick(10) && owner != null && isAllowedDimension() && isAllowedLocation()) {
            ServerWorld serverLevel = (ServerWorld) level;
            AxisAlignedBB aabb = getBoundingBox(serverLevel);
            List<ServerPlayerEntity> players = serverLevel.getEntitiesOfClass(ServerPlayerEntity.class, aabb);

            if (!players.isEmpty()) {
                List<UUID> allowed = allowedPlayers();
                for (ServerPlayerEntity player : players) {
                    if (!this.canAccess(player) && !allowed.contains(player.getUUID())) {
                        doRespawn(serverLevel.getServer(), player);
                    }
                }
            }
        }
    }

    @Override
    public void updateContainer(PacketBuffer packetBuffer) {
        super.updateContainer(packetBuffer);
        packetBuffer.writeBoolean(isAllowedDimension());
        packetBuffer.writeBoolean(isAllowedLocation());
    }

    public boolean isAllowedDimension() {
        if (level != null) {
            if (allowedDimensions == null) {
                List<RegistryKey<World>> temp = new ArrayList<>();
                List<? extends String> allowed = ConfigManager.SERVER.expulsionAllowedDimensions.get();
                for (String key : allowed) {
                    temp.add(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(key)));
                }
                allowedDimensions = ImmutableList.copyOf(temp);
            }
            return allowedDimensions.contains(level.dimension());
        }
        return false;
    }

    public boolean isAllowedLocation() {
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;
            int spawnRadius = serverLevel.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS);
            int configRadius = ConfigManager.SERVER.expulsionWorldSpawnRadius.get();

            AxisAlignedBB bb = new AxisAlignedBB(serverLevel.getSharedSpawnPos());
            AxisAlignedBB area = bb.inflate(Math.max(configRadius, spawnRadius));

            AxisAlignedBB workArea = getBoundingBox(serverLevel);
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

            // player has a spawn position, is the spawn position in the pylon's work area?
            Vector3d spawnPosition = Vector3d.atBottomCenterOf(dummyPlayer.blockPosition());
            if (getBoundingBox(actualLevel).contains(spawnPosition)) {
                return;
            }
        }

        while (!actualLevel.noCollision(dummyPlayer) && dummyPlayer.getY() < 256.0D) {
            dummyPlayer.setPos(dummyPlayer.getX(), dummyPlayer.getY() + 1.0D, dummyPlayer.getZ());
        }

        player.teleportTo(actualLevel, dummyPlayer.getX(), dummyPlayer.getY(), dummyPlayer.getZ(), dummyPlayer.yRot, dummyPlayer.xRot);
        player.sendMessage(new TranslationTextComponent(TranslationKey.chat("expelled"), getOwnerName()).withStyle(TextFormatting.RED), player.getUUID());
    }
}
