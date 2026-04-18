package net.permutated.pylons.machines.base;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public class PylonEnergyStorage extends SimpleEnergyHandler {

    private final Runnable listener;

    public PylonEnergyStorage(Runnable listener, int capacity, int maxRecieve) {
        super(capacity, maxRecieve, 0);
        this.listener = listener;
    }

    @Override
    protected void onEnergyChanged(int previousAmount) {
        listener.run();
    }
}
