package net.permutated.pylons.machines.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.energy.EnergyStorage;

import javax.annotation.Nullable;

public class PylonEnergyStorage extends EnergyStorage {

    private final Runnable listener;

    public PylonEnergyStorage(Runnable listener, int capacity, int maxRecieve) {
        super(capacity, maxRecieve, 0);
        this.listener = listener;
    }

    public void onEnergyChanged() {
        listener.run();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int rc = super.receiveEnergy(maxReceive, simulate);
        if (rc > 0 && !simulate) {
            onEnergyChanged();
        }
        return rc;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int rc = super.extractEnergy(maxExtract, simulate);
        if (rc > 0 && !simulate) {
            onEnergyChanged();
        }
        return rc;
    }

    public boolean consumeEnergy(int request, boolean simulate) {
        int consumed = Math.max(0, request);
        if (this.energy > consumed) {
            if (!simulate) {
                this.energy -= consumed;
                onEnergyChanged();
            }
            return true;
        }
        return false;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, @Nullable Tag nbt) {
        if (nbt != null) {
            super.deserializeNBT(provider, nbt);
        }
    }
}
