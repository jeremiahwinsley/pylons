package net.permutated.pylons.block;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.tile.AbstractPylonTile;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class AbstractPylonBlock extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.or(
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
        Block.box(1.0D, 3.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    );

    protected AbstractPylonBlock() {
        super(Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE));
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated
    public VoxelShape getShape(BlockState blockState, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("java:S3038") // method is required here to override default from IForgeBlock
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    public abstract IContainerFactory<AbstractPylonContainer> containerFactory();

    @Override
    public void setPlacedBy(World level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
        if (!level.isClientSide && entity instanceof PlayerEntity) {
            TileEntity tileEntity = level.getBlockEntity(blockPos);
            if (tileEntity instanceof AbstractPylonTile) {
                AbstractPylonTile pylonTile = (AbstractPylonTile) tileEntity;
                pylonTile.setOwner(entity.getUUID());
            }
        }
    }

    @Override
    public void destroy(IWorld world, BlockPos blockPos, BlockState blockState) {
        Optional.ofNullable(world.getBlockEntity(blockPos))
            .map(AbstractPylonTile.class::cast)
            .ifPresent(AbstractPylonTile::dropItems);

        super.destroy(world, blockPos, blockState);
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof AbstractPylonTile)
            {
                ((AbstractPylonTile) tileentity).dropItems();
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult)
    {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AbstractPylonTile) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent(getDescriptionId());
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                        return containerFactory().create(i, playerInventory, buffer.writeBlockPos(pos));
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
            } else {
                Pylons.LOGGER.error("tile entity not instance of AbstractPylonTile");
                return ActionResultType.FAIL;
            }
        }
        return ActionResultType.SUCCESS;
    }

    protected IFormattableTextComponent translate(String key) {
        return new TranslationTextComponent(TranslationKey.tooltip(key)).withStyle(TextFormatting.GRAY);
    }
}
