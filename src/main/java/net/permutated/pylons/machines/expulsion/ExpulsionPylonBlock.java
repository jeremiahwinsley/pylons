package net.permutated.pylons.machines.expulsion;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.IContainerFactory;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.machines.base.AbstractPylonBlock;
import net.permutated.pylons.machines.base.AbstractPylonContainer;
import net.permutated.pylons.machines.base.AbstractPylonTile;

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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        tooltip.add(translate("expulsion1"));
        tooltip.add(translate("expulsion2"));
        tooltip.add(translate("expulsion3"));
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        if (Boolean.TRUE.equals(ConfigManager.SERVER.expulsionPylonCanExplode.get())) {
            return 6.0F;
        } else {
            return super.getExplosionResistance(state, level, pos, explosion);
        }
    }
}
