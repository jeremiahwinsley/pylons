package net.permutated.pylons.machines.interdiction;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.IContainerFactory;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.machines.base.AbstractPylonBlock;
import net.permutated.pylons.machines.base.AbstractPylonContainer;
import net.permutated.pylons.machines.base.AbstractPylonTile;

import javax.annotation.Nullable;
import java.util.List;

public class InterdictionPylonBlock extends AbstractPylonBlock {
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InterdictionPylonTile(pos, state);
    }

    @Override
    public BlockEntityType<? extends AbstractPylonTile> getTileType() {
        return ModRegistry.INTERDICTION_PYLON_TILE.get();
    }

    @Override
    public IContainerFactory<AbstractPylonContainer> containerFactory() {
        return InterdictionPylonContainer::new;
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        tooltip.add(translate("interdiction1"));
        tooltip.add(translate("interdiction2"));
    }
}
