package net.permutated.pylons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.network.IContainerFactory;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;

import net.permutated.pylons.inventory.container.InfusionPylonContainer;
import net.permutated.pylons.tile.AbstractPylonTile;
import net.permutated.pylons.tile.InfusionPylonTile;

import javax.annotation.Nullable;
import java.util.List;

public class InfusionPylonBlock extends AbstractPylonBlock {
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionPylonTile(pos, state);
    }

    @Override
    public BlockEntityType<? extends AbstractPylonTile> getTileType() {
        return ModRegistry.INFUSION_PYLON_TILE.get();
    }

    @Override
    public IContainerFactory<AbstractPylonContainer> containerFactory() {
        return InfusionPylonContainer::new;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, reader, tooltip, flagIn);

        tooltip.add(translate("infusion1"));
        tooltip.add(translate("infusion2"));
        tooltip.add(translate("infusion3"));
    }
}
