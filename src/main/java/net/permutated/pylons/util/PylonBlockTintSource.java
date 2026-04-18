package net.permutated.pylons.util;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.machines.base.AbstractPylonTile;

public class PylonBlockTintSource implements BlockTintSource {
    @Override
    public int color(BlockState blockState) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof AbstractPylonTile tile) {
            return tile.getColor();
        }
        return color(state);
    }
}
