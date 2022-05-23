package net.permutated.pylons.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

public abstract class AbstractPylonTile extends TileEntity implements ITickableTileEntity {

    protected AbstractPylonTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
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
        if (owner != null && level instanceof ServerWorld) {
            ChunkManager.unloadChunk(owner, (ServerWorld) level, getBlockPos());
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

    public boolean canAccess(PlayerEntity player) {
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

    protected static void dropItems(@Nullable World world, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemstack = itemHandler.getStackInSlot(i);

            if (itemstack.getCount() > 0 && world != null) {
                InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
            }
        }
    }

    public String getOwnerName() {
        String lastKnown = owner == null ? null : UsernameCache.getLastKnownUsername(owner);
        return StringUtils.defaultString(lastKnown, Constants.UNKNOWN);
    }

    /**
     * Serialize data to be sent to the GUI on the client.
     *
     * Overrides MUST call the super method first to ensure correct deserialization.
     * @param packetBuffer the packet ready to be filled
     */
    public void updateContainer(PacketBuffer packetBuffer) {
        String lastKnown = UsernameCache.getLastKnownUsername(owner);
        String username = StringUtils.defaultString(lastKnown, Constants.UNKNOWN);

        packetBuffer.writeBlockPos(worldPosition);
        packetBuffer.writeInt(username.length());
        packetBuffer.writeUtf(username);
    }

    // Save TE data to disk
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put(Constants.NBT.INV, itemStackHandler.serializeNBT());
        tag.put(Constants.NBT.RANGE, range.serializeNBT());
        writeFields(tag);
        return super.save(tag);
    }

    // Write TE data to a provided CompoundNBT
    private void writeFields(CompoundNBT tag) {
        tag.putBoolean(Constants.NBT.ENABLED, shouldWork);
        if (owner != null) {
            tag.putUUID(Constants.NBT.OWNER, owner);
        }
    }

    // Load TE data from disk
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        itemStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.INV));
        range.deserializeNBT(tag.getCompound(Constants.NBT.RANGE));
        readFields(tag);
        super.load(state, tag);
    }

    // Read TE data from a provided CompoundNBT
    private void readFields(@Nullable CompoundNBT tag) {
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
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.put(Constants.NBT.RANGE, range.serializeNBT());
        writeFields(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        if (tag != null) {
            range.deserializeNBT(tag.getCompound(Constants.NBT.RANGE));
        }
        readFields(tag);
    }

    // Called whenever a block update happens on the client
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, -1, getUpdateTag());
    }

    // Handles the update packet received from the server
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.handleUpdateTag(this.getBlockState(), pkt.getTag());
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
