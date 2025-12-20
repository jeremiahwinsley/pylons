package net.permutated.pylons.compat.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface Harvestable {
    Block getBlock();
    boolean isHarvestable(Level level, BlockPos blockPos, BlockState blockState);
    List<ItemStack> harvest(Level level, BlockPos blockPos, BlockState blockState);
}
