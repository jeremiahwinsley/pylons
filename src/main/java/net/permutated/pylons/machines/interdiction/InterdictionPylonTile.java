package net.permutated.pylons.machines.interdiction;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
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
    protected boolean isItemValid(ItemResource resource) {
        return resource.getItem() instanceof MobFilterCard || resource.getItem() instanceof LifelessFilterCard;
    }

    private boolean dirty = true;

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(20) && dirty) {
            HashSet<Identifier> filters = new HashSet<>();
            for (int i = 0; i < itemStackHandler.size(); i++) {
                ItemResource resource = itemStackHandler.getResource(i);
                if (!resource.isEmpty() && resource.getItem() instanceof MobFilterCard) {
                    EntityComponent data = resource.get(ModRegistry.ENTITY_COMPONENT);
                    if (data != null) {
                        filters.add(data.registryKey());
                    }
                } else if (!resource.isEmpty() && resource.getItem() instanceof LifelessFilterCard) {
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
