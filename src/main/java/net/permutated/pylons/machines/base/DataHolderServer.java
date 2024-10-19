package net.permutated.pylons.machines.base;

public class DataHolderServer implements DataHolder {
    final AbstractPylonTile blockEntity;
    public DataHolderServer(AbstractPylonTile blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public int getEnabled() {
        return blockEntity.shouldWork() ? 1 : 0;
    }

    @Override
    public int getRange() {
        return blockEntity.getSelectedRange();
    }

    @Override
    public int getEnergy() {
        return blockEntity.energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergy() {
        return blockEntity.energyStorage.getMaxEnergyStored();
    }
}
