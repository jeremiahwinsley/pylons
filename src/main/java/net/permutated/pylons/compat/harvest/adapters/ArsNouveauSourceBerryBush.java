package net.permutated.pylons.compat.harvest.adapters;

import com.hollingsworth.arsnouveau.common.block.SourceBerryBush;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.compat.harvest.Harvestable;

public class ArsNouveauSourceBerryBush implements Harvestable {
    public Block getBlock() {
        return BlockRegistry.SOURCEBERRY_BUSH.get();
    }

    public boolean isHarvestable(BlockState blockState) {
        return blockState.getValue(SourceBerryBush.AGE) == 3;
    }

    public ItemStack harvest(Level level, BlockPos blockPos, BlockState blockState) {
        level.setBlock(blockPos, blockState.setValue(SourceBerryBush.AGE, 1), Block.UPDATE_CLIENTS);
        return new ItemStack(BlockRegistry.SOURCEBERRY_BUSH, 2);
    }
}
