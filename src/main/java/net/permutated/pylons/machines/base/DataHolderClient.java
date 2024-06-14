package net.permutated.pylons.machines.base;

public class DataHolderClient implements DataHolder {
    private int enabled;
    private int range;

    @Override
    public int getEnabled() {
        return enabled;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setRange(int range) {
        this.range = range;
    }
}
