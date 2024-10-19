package net.permutated.pylons.machines.base;

public class DataHolderClient implements DataHolder {
    private int enabled;
    private int range;
    private int energy;
    private int maxEnergy;

    @Override
    public int getEnabled() {
        return enabled;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }
}
