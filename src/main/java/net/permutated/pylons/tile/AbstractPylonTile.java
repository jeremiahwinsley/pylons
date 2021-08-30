package net.permutated.pylons.tile;

import net.minecraft.block.BlockState;
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
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.permutated.pylons.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

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

    private long lastTicked = 0L;

    public boolean canTick(final int every) {
        long gameTime = level != null ? level.getGameTime() : 0L;
        if (gameTime % every == 0 && gameTime != lastTicked) {
            lastTicked = gameTime;
            return true;
        } else {
            return false;
        }
    }

    protected static void dropItems(@Nullable World world, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemstack = itemHandler.getStackInSlot(i);

            if (itemstack.getCount() > 0 && world != null) {
                InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
            }
        }
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
        writeOwner(tag);
        return super.save(tag);
    }

    // Write TE data to a provided CompoundNBT
    private void writeOwner(CompoundNBT tag) {
        if (owner != null) {
            tag.putUUID(Constants.NBT.OWNER, owner);
        }
    }

    // Load TE data from disk
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        itemStackHandler.deserializeNBT(tag.getCompound(Constants.NBT.INV));
        readOwner(tag);
        super.load(state, tag);
    }

    // Read TE data from a provided CompoundNBT
    private void readOwner(CompoundNBT tag) {
        if (tag.hasUUID(Constants.NBT.OWNER)) {
            owner = tag.getUUID(Constants.NBT.OWNER);
        }
    }

    // Called whenever a client loads a new chunk
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        writeOwner(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        readOwner(tag);
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

    public class PylonEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

        public PylonEnergyStorage(int capacity, int maxTransfer) {
            super(capacity, maxTransfer);
        }

        public void onEnergyChanged() {
            setChanged();
        }

        public void setEnergy(int energy) {
            this.energy = energy;
            onEnergyChanged();
        }

        public void addEnergy(int energy) {
            this.energy += energy;
            if (this.energy > getMaxEnergyStored()) {
                this.energy = getEnergyStored();
            }
            onEnergyChanged();
        }

        public void consumeEnergy(int energy) {
            this.energy -= energy;
            if (this.energy < 0) {
                this.energy = 0;
            }
            onEnergyChanged();
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(Constants.NBT.ENERGY, getEnergyStored());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            setEnergy(nbt.getInt(Constants.NBT.ENERGY));
        }
    }
}
