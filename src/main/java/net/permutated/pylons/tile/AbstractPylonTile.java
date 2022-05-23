package net.permutated.pylons.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.permutated.pylons.util.ChunkManager;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.Range;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractPylonTile extends BlockEntity {

    protected AbstractPylonTile(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
    }

    public static final int SLOTS = 9;

    protected final ItemStackHandler itemStackHandler = new PylonItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return AbstractPylonTile.this.isItemValid(stack);
        }
    };

    protected abstract boolean isItemValid(ItemStack stack);

    protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemStackHandler);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public void dropItems() {
        AbstractPylonTile.dropItems(level, worldPosition, itemStackHandler);
    }

    public void removeChunkloads() {
        if (owner != null && level instanceof ServerLevel serverLevel) {
            ChunkManager.unloadChunk(owner, serverLevel, getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    protected UUID owner = null;
    protected String ownerName = null;

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.setChanged();
    }

    public boolean canAccess(Player player) {
        return Objects.equals(player.getUUID(), owner) || owner == null || player.hasPermissions(2);
    }

    private long lastTicked = 0L;

    public boolean canTick(final int every) {
        long gameTime = level != null ? level.getGameTime() : 0L;
        if (offset(gameTime) % every == 0 && gameTime != lastTicked && shouldWork()) {
            lastTicked = gameTime;
            return true;
        } else {
            return false;
        }
    }

    int offset = 0;
    /**
     * Add a random offset between 0 and 19 ticks.
     * This is generated once per block entity on the first tick.
     * @param gameTime the current game time
     * @return the tick delay with the saved offset
     */
    protected long offset(final long gameTime) {
        if (offset == 0) offset += ThreadLocalRandom.current().nextInt(0, 20);
        return gameTime + offset;
    }

    public abstract void tick();

    @SuppressWarnings("java:S1172") // unused arguments are required
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (blockEntity instanceof AbstractPylonTile pylonTile && pylonTile.shouldWork()) {
            pylonTile.tick();
        }
    }

    protected static void dropItems(@Nullable Level world, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemstack = itemHandler.getStackInSlot(i);

            if (itemstack.getCount() > 0 && world != null) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
            }
        }
    }

    public String getOwnerName() {
        String lastKnown = owner == null ? null : UsernameCache.getLastKnownUsername(owner);
        return StringUtils.defaultString(lastKnown, Constants.UNKNOWN);
    }

    /**
     * Serialize data to be sent to the GUI on the client.
     * <p>
     * Overrides MUST call the super method first to ensure correct deserialization.
     *
     * @param packetBuffer the packet ready to be filled
     */
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        String username = getOwnerName();

        packetBuffer.writeBlockPos(worldPosition);
        packetBuffer.writeInt(username.length());
        packetBuffer.writeUtf(username);
    }

    // Save TE data to disk
    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put(Constants.NBT.INV, itemStackHandler.serializeNBT());
        tag.put(Constants.NBT.RANGE, range.serializeNBT());
        writeFields(tag);
    }

    // Write TE data to a provided CompoundNBT
    private void writeFields(CompoundTag tag) {
        tag.putBoolean(Constants.NBT.ENABLED, shouldWork);
        if (owner != null) {
            tag.putUUID(Constants.NBT.OWNER, owner);
        }
    }

    // Load TE data from disk
    @Override
    public void load(CompoundTag tag) {
        itemStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.INV));
        range.deserializeNBT(tag.getCompound(Constants.NBT.RANGE));
        readFields(tag);
        super.load(tag);
    }

    // Read TE data from a provided CompoundNBT
    private void readFields(@Nullable CompoundTag tag) {
        // check for NBT before loading, so that existing blocks don't get disabled
        if (tag != null && tag.contains(Constants.NBT.ENABLED)) {
            shouldWork = tag.getBoolean(Constants.NBT.ENABLED);
        }
        if (tag != null && tag.hasUUID(Constants.NBT.OWNER)) {
            owner = tag.getUUID(Constants.NBT.OWNER);
        }
    }

    // Called whenever a client loads a new chunk
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put(Constants.NBT.RANGE, range.serializeNBT());
        writeFields(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nullable CompoundTag tag) {
        if (tag != null) {
            range.deserializeNBT(tag.getCompound(Constants.NBT.RANGE));
        }
        readFields(tag);
    }

    // Called whenever a block update happens on the client
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    // Handles the update packet received from the server
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.handleUpdateTag(pkt.getTag());
    }

    public class PylonItemStackHandler extends ItemStackHandler {
        public PylonItemStackHandler(int size) {
            super(size);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    }

    protected boolean shouldWork = true;

    public boolean shouldWork() {
        return shouldWork;
    }

    public void handleWorkPacket() {
        if (this.level != null) {
            shouldWork = !shouldWork;
            this.setChanged();
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);

            if (!shouldWork) removeChunkloads();
        }
    }

    protected final Range range = new Range(getRange());

    protected byte[] getRange() {
        return new byte[]{1};
    }

    public int getSelectedRange() {
        return range.get();
    }

    public void handleRangePacket() {
        if (getRange().length > 1 && this.level != null) {
            this.range.next();
            this.setChanged();
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        }
    }
}
