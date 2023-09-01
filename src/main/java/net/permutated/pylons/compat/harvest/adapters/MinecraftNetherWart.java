package net.permutated.pylons.compat.harvest.adapters;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.compat.harvest.Harvestable;

public class MinecraftNetherWart implements Harvestable {
    @Override
    public Block getBlock() {
        return Blocks.NETHER_WART;
    }

    @Override
    public boolean isHarvestable(BlockState blockState) {
        return blockState.getValue(NetherWartBlock.AGE) == 3;
    }

    @Override
    public ItemStack harvest(Level level, BlockPos blockPos, BlockState blockState) {
        level.setBlock(blockPos, blockState.setValue(NetherWartBlock.AGE, 0), Block.UPDATE_CLIENTS);
        return new ItemStack(Items.NETHER_WART, 3);
    }
}
