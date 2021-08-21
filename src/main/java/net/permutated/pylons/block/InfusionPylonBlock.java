package net.permutated.pylons.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.network.IContainerFactory;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;

import net.permutated.pylons.inventory.container.InfusionPylonContainer;
import net.permutated.pylons.tile.InfusionPylonTile;

import javax.annotation.Nullable;
import java.util.List;

public class InfusionPylonBlock extends AbstractPylonBlock {
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InfusionPylonTile();
    }

    @Override
    public IContainerFactory<AbstractPylonContainer> containerFactory() {
        return InfusionPylonContainer::new;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, reader, tooltip, flagIn);

        tooltip.add(translate("infusion1"));
        tooltip.add(translate("infusion2"));
        tooltip.add(translate("infusion3"));
    }
}
