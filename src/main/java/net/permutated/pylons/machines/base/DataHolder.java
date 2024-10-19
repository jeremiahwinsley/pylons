package net.permutated.pylons.machines.base;

import net.minecraft.util.Mth;

public interface DataHolder {
    int getEnabled();
    int getRange();
    int getEnergy();
    int getMaxEnergy();

    default void setEnabled(int enabled) {}
    default void setRange(int range) {}
    default void setEnergy(int energy) {}
    default void setMaxEnergy(int maxEnergy) {}

    default float getEnergyFraction() {
        if (getEnergy() == 0) {
            return 0f;
        } else {
            return ((float) Mth.clamp(getEnergy(), 0, getMaxEnergy())) / getMaxEnergy();
        }
    }
}
