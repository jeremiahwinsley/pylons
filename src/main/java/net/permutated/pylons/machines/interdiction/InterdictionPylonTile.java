package net.permutated.pylons.machines.interdiction;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.EntityComponent;
import net.permutated.pylons.item.LifelessFilterCard;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.machines.base.AbstractPylonTile;
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
        return stack.getItem() instanceof MobFilterCard || stack.getItem() instanceof LifelessFilterCard;
    }

    private boolean dirty = true;

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(20) && dirty) {
            HashSet<ResourceLocation> filters = new HashSet<>();
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof MobFilterCard) {
                    EntityComponent data = stack.get(ModRegistry.ENTITY_COMPONENT);
                    if (data != null) {
                        filters.add(data.registryKey());
                    }
                } else if (!stack.isEmpty() && stack.getItem() instanceof LifelessFilterCard) {
                    SpawnManager.registerLifeless(serverLevel, worldPosition);
                    dirty = false;
                    return;
                }
            }

            SpawnManager.register(serverLevel, worldPosition, range, filters);
            dirty = false;
        }
    }

    @Override
    public void removeChunkloads() {
        if (level instanceof ServerLevel serverLevel) {
            SpawnManager.unregister(serverLevel, worldPosition);
            this.dirty = true;
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.dirty = true;
    }
}
