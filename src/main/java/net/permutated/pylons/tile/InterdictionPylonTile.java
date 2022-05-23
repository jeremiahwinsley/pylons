package net.permutated.pylons.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.SpawnManager;

import java.util.HashSet;

public class InterdictionPylonTile extends AbstractPylonTile {

    public InterdictionPylonTile() {
        super(ModRegistry.INTERDICTION_PYLON_TILE.get());
    }

    @Override
    protected byte[] getRange() {
        return new byte[]{1, 3, 5};
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof MobFilterCard;
    }

    private boolean dirty = true;

    @Override
    public void tick() {
        if (level instanceof ServerWorld && canTick(20) && dirty) {
            ServerWorld serverLevel = (ServerWorld) level;

            HashSet<String> filters = new HashSet<>();
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof MobFilterCard) {
                    CompoundNBT tag = stack.getTagElement(Pylons.MODID);
                    if (tag != null && tag.contains(Constants.NBT.REGISTRY)) {
                        filters.add(tag.getString(Constants.NBT.REGISTRY));
                    }
                }
            }

            SpawnManager.register(serverLevel, worldPosition, range, filters);
            dirty = false;
        }
    }

    @Override
    public void removeChunkloads() {
        super.removeChunkloads();
        if (level instanceof ServerWorld) {
            SpawnManager.unregister((ServerWorld) level, worldPosition);
        }
    }

    @Override
    public void handleWorkPacket() {
        super.handleWorkPacket();
        if (!shouldWork && level instanceof ServerWorld) {
            SpawnManager.unregister((ServerWorld) level, worldPosition);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.dirty = true;
    }
}
