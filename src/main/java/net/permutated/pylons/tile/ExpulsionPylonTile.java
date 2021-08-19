package net.permutated.pylons.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ExpulsionPylonTile extends AbstractPylonTile {
    public static final int SLOTS = 9;

    private final ItemStackHandler itemStackHandler = new PylonItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof PlayerFilterCard;
        }
    };

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemStackHandler);

    public ExpulsionPylonTile() {
        super(ModRegistry.EXPULSION_PYLON_TILE.get());
    }

    @Override
    public int getInventorySize() {
        return this.itemStackHandler.getSlots();
    }

    @Override
    public void dropItems() {
        AbstractPylonTile.dropItems(level, worldPosition, itemStackHandler);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put(Constants.NBT.INV, itemStackHandler.serializeNBT());
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        itemStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.INV));
        super.load(state, tag);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && canTick(10) && owner != null) {
            Chunk chunk = level.getChunkAt(worldPosition);
            List<ServerPlayerEntity> players = Arrays.stream(chunk.getEntitySections())
                .map(multiMap -> multiMap.find(ServerPlayerEntity.class))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

            List<UUID> whitelist = new ArrayList<>();
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof PlayerFilterCard) {
                    CompoundNBT tag = stack.getTagElement(Pylons.MODID);
                    if (tag != null && tag.hasUUID(Constants.NBT.UUID)) {
                        whitelist.add(tag.getUUID(Constants.NBT.UUID));
                    }
                }
            }

            MinecraftServer server = level.getServer();
            for (ServerPlayerEntity player : players) {
                if (server != null
                    && !player.hasPermissions(2)
                    && !player.getUUID().equals(owner)
                    && !whitelist.contains(player.getUUID())) {
                    doRespawn(server, player);
                }
            }
        }
    }

    private void doRespawn(MinecraftServer server, ServerPlayerEntity player) {

        boolean alive = true;
        BlockPos respawnPosition = player.getRespawnPosition();
        float respawnAngle = player.getRespawnAngle();
        boolean flag = player.isRespawnForced();

        ServerWorld respawnLevel = server.getLevel(player.getRespawnDimension());

        Optional<Vector3d> optional;
        if (respawnLevel != null && respawnPosition != null) {
            optional = PlayerEntity.findRespawnPositionAndUseSpawnBlock(respawnLevel, respawnPosition, respawnAngle, flag, alive);
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
                Vector3d vector3d1 = Vector3d.atBottomCenterOf(respawnPosition).subtract(spawnPos).normalize();
                actualAngle = (float) MathHelper.wrapDegrees(MathHelper.atan2(vector3d1.z, vector3d1.x) * (180F / (float) Math.PI) - 90.0D);
            }

            dummyPlayer.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, actualAngle, 0.0F);
            dummyPlayer.setRespawnPosition(actualLevel.dimension(), respawnPosition, respawnAngle, flag, false);
        } else if (respawnPosition != null) {
            player.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
        }

        while (!actualLevel.noCollision(dummyPlayer) && dummyPlayer.getY() < 256.0D) {
            dummyPlayer.setPos(dummyPlayer.getX(), dummyPlayer.getY() + 1.0D, dummyPlayer.getZ());
        }

        player.teleportTo(actualLevel, dummyPlayer.getX(), dummyPlayer.getY(), dummyPlayer.getZ(), dummyPlayer.yRot, dummyPlayer.xRot);
    }
}
