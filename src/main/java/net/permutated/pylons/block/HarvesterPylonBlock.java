package net.permutated.pylons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.network.IContainerFactory;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.inventory.container.HarvesterPylonContainer;
import net.permutated.pylons.tile.AbstractPylonTile;
import net.permutated.pylons.tile.HarvesterPylonTile;

import javax.annotation.Nullable;
import java.util.List;

public class HarvesterPylonBlock extends AbstractPylonBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public HarvesterPylonBlock() {
        super();
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    @SuppressWarnings("java:S1874") // mojang deprecated
    public FluidState getFluidState(BlockState blockState) {
        return Boolean.TRUE.equals(blockState.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(clickedPos);
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public IContainerFactory<AbstractPylonContainer> containerFactory() {
        return HarvesterPylonContainer::new;
    }

    @Override
    public BlockEntityType<? extends AbstractPylonTile> getTileType() {
        return ModRegistry.HARVESTER_PYLON_TILE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HarvesterPylonTile(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, reader, tooltip, flagIn);

        tooltip.add(translate("harvester1"));
        tooltip.add(translate("harvester2"));
        tooltip.add(translate("harvester3"));
    }
}
