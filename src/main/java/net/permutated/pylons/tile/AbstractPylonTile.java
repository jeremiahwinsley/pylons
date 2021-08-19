package net.permutated.pylons.tile;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.permutated.pylons.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractPylonTile extends TileEntity implements ITickableTileEntity {

    protected AbstractPylonTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    protected UUID owner = null;
    protected String ownerName = null;

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.setChanged();
    }

    @Nullable
    public String getOwnerName() {
        return this.ownerName;
    }

    public abstract int getInventorySize();

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

    public abstract void dropItems();

    protected static void dropItems(@Nullable World world, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemstack = itemHandler.getStackInSlot(i);

            if (itemstack.getCount() > 0 && world != null) {
                InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
            }
        }
    }

    // Save TE data to disk
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        writeOwner(tag);
        return super.save(tag);
    }

    // Write TE data to a provided CompoundNBT
    private void writeOwner(CompoundNBT tag) {
        if (owner != null) {
            tag.putUUID(Constants.NBT.OWNER, owner);
        }
    }

    // Write username to a provided CompoundNBT
    // Only used for server -> client sync
    private void writeUsername(CompoundNBT tag) {
        if (owner != null) {
            ownerName = UsernameCache.getLastKnownUsername(owner);
            if (ownerName != null) {
                tag.putString(Constants.NBT.NAME, ownerName);
            }
        }
    }

    // Load TE data from disk
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        readOwner(tag);
        super.load(state, tag);
    }

    // Read TE data from a provided CompoundNBT
    private void readOwner(CompoundNBT tag) {
        if (tag.hasUUID(Constants.NBT.OWNER)) {
            owner = tag.getUUID(Constants.NBT.OWNER);
        }
    }

    // Read username from a provided CompoundNBT
    // Only used for server -> client sync
    private void readUsername(CompoundNBT tag) {
        if (tag.contains(Constants.NBT.NAME)) {
            ownerName = tag.getString(Constants.NBT.NAME);
        }
    }

    // Called whenever a client loads a new chunk
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        writeOwner(tag);
        writeUsername(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        readOwner(tag);
        readUsername(tag);
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
