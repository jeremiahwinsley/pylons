package net.permutated.pylons.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.SpawnManager;

import java.util.HashSet;

public class InterdictionPylonTile extends AbstractPylonTile {

    public InterdictionPylonTile(BlockPos pos, BlockState state) {
        super(ModRegistry.INTERDICTION_PYLON_TILE.get(), pos, state);
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
        if (level instanceof ServerLevel serverLevel && canTick(20) && dirty) {
            HashSet<String> filters = new HashSet<>();
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof MobFilterCard) {
                    CompoundTag tag = stack.getTagElement(Pylons.MODID);
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
        if (level instanceof ServerLevel serverLevel) {
            SpawnManager.unregister(serverLevel, worldPosition);
        }
    }

    @Override
    public void handleWorkPacket() {
        super.handleWorkPacket();
        if (!shouldWork && level instanceof ServerLevel serverLevel) {
            SpawnManager.unregister(serverLevel, worldPosition);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.dirty = true;
    }
}
