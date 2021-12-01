package net.permutated.pylons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.IContainerFactory;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.inventory.container.ExpulsionPylonContainer;
import net.permutated.pylons.tile.AbstractPylonTile;
import net.permutated.pylons.tile.ExpulsionPylonTile;

import javax.annotation.Nullable;
import java.util.List;

public class ExpulsionPylonBlock extends AbstractPylonBlock {
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExpulsionPylonTile(pos, state);
    }

    @Override
    public BlockEntityType<? extends AbstractPylonTile> getTileType() {
        return ModRegistry.EXPULSION_PYLON_TILE.get();
    }

    @Override
    public IContainerFactory<AbstractPylonContainer> containerFactory() {
        return ExpulsionPylonContainer::new;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, reader, tooltip, flagIn);

        tooltip.add(translate("expulsion1"));
        tooltip.add(translate("expulsion2"));
        tooltip.add(translate("expulsion3"));
    }
}
