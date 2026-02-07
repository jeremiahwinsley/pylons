package net.permutated.pylons.compat.harvest.adapters;

import com.cobblemon.mod.common.block.BerryBlock;
import com.cobblemon.mod.common.block.entity.BerryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.compat.harvest.Harvestable;

import java.util.Collection;
import java.util.Collections;

public class CobblemonBerryBush implements Harvestable {
    @Override
    public Class<? extends Block> getBlock() {
        return BerryBlock.class;
    }

    @Override
    public boolean isHarvestable(BlockState blockState) {
        return blockState.getValue(BerryBlock.Companion.getAGE()) == BerryBlock.FRUIT_AGE;
    }

    @Override
    public Collection<ItemStack> harvest(Level level, BlockPos blockPos, BlockState blockState) {
        BlockEntity entity = level.getBlockEntity(blockPos);
        if (entity instanceof BerryBlockEntity berry) {
            return berry.harvest(level, blockState, blockPos);
        }
        return Collections.emptyList();
    }
}
