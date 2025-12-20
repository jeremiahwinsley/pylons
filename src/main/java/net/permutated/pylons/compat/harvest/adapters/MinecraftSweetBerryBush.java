package net.permutated.pylons.compat.harvest.adapters;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.compat.harvest.Harvestable;

import java.util.List;

public class MinecraftSweetBerryBush implements Harvestable {
    @Override
    public Block getBlock() {
        return Blocks.SWEET_BERRY_BUSH;
    }

    @Override
    public boolean isHarvestable(Level level, BlockPos pos, BlockState blockState) {
        return blockState.getValue(SweetBerryBushBlock.AGE) == 3;
    }

    @Override
    public List<ItemStack> harvest(Level level, BlockPos blockPos, BlockState blockState) {
        level.setBlock(blockPos, blockState.setValue(SweetBerryBushBlock.AGE, 1), Block.UPDATE_CLIENTS);
        return List.of(new ItemStack(Items.SWEET_BERRIES, 2));
    }
}
