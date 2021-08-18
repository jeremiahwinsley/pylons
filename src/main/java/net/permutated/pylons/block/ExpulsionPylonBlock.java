package net.permutated.pylons.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.network.IContainerFactory;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.inventory.container.ExpulsionPylonContainer;
import net.permutated.pylons.tile.ExpulsionPylonTile;

public class ExpulsionPylonBlock extends AbstractPylonBlock {
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ExpulsionPylonTile();
    }

    @Override
    public IContainerFactory<AbstractPylonContainer> containerFactory() {
        return ExpulsionPylonContainer::new;
    }
}
