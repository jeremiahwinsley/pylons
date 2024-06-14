package net.permutated.pylons.machines.base;

public interface DataHolder {
    int getEnabled();
    int getRange();

    default void setEnabled(int enabled) {}
    default void setRange(int range) {}
}
