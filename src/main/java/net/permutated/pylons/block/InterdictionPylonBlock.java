package net.permutated.pylons.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.network.IContainerFactory;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.inventory.container.InterdictionPylonContainer;
import net.permutated.pylons.tile.InterdictionPylonTile;

import javax.annotation.Nullable;
import java.util.List;

public class InterdictionPylonBlock extends AbstractPylonBlock {
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InterdictionPylonTile();
    }

    @Override
    public IContainerFactory<AbstractPylonContainer> containerFactory() {
        return InterdictionPylonContainer::new;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, reader, tooltip, flagIn);

        tooltip.add(translate("interdiction1"));
        tooltip.add(translate("interdiction2"));
    }
}
