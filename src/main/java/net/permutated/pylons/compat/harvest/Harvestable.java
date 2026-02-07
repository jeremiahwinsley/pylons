package net.permutated.pylons.compat.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;

public interface Harvestable {
    Class<? extends Block> getBlock();
    boolean isHarvestable(BlockState blockState);
    Collection<ItemStack> harvest(Level level, BlockPos blockPos, BlockState blockState);
}
