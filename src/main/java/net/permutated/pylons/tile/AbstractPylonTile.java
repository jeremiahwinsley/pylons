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
        this.ownerName = UsernameCache.getLastKnownUsername(owner);
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

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);

        if (owner != null) {
            tag.putUUID(Constants.NBT.OWNER, owner);
        }

        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        if (tag.hasUUID(Constants.NBT.OWNER)) {
            this.setOwner(tag.getUUID(Constants.NBT.OWNER));
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (level != null) {
            BlockState blockState = level.getBlockState(worldPosition);
            handleUpdateTag(blockState, pkt.getTag());
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
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
