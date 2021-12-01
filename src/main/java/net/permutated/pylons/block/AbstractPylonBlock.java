package net.permutated.pylons.block;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkHooks;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.tile.AbstractPylonTile;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class AbstractPylonBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
        Block.box(1.0D, 3.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    );

    protected AbstractPylonBlock() {
        super(Properties.of(Material.METAL).strength(2F, 1200F));
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated
    public VoxelShape getShape(BlockState blockState, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public abstract IContainerFactory<AbstractPylonContainer> containerFactory();

    @SuppressWarnings("java:S1452") // wildcard required here
    public abstract BlockEntityType<? extends AbstractPylonTile> getTileType();

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == getTileType() ? AbstractPylonTile::tick : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
        if (!level.isClientSide && entity instanceof Player) {
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            if (tileEntity instanceof AbstractPylonTile pylonTile) {
                pylonTile.setOwner(entity.getUUID());
            }
        }
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState blockState) {
        Optional.ofNullable(world.getBlockEntity(blockPos))
            .map(AbstractPylonTile.class::cast)
            .ifPresent(AbstractPylonTile::dropItems);

        super.destroy(world, blockPos, blockState);
    }

    /**
     * Block should only be broken by the owner or ops.
     * @param event the BreakEvent
     */
    public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        if (event.getPlayer().hasPermissions(2)) {
            return;
        }

        if (event.getState().getBlock() instanceof AbstractPylonBlock) {
            BlockEntity tileEntity = event.getWorld().getBlockEntity(event.getPos());

            if (tileEntity instanceof AbstractPylonTile pylonTile) {
                if (!event.getPlayer().getUUID().equals(pylonTile.getOwner())) {
                    event.setCanceled(true);
                }
            }
        }

    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof AbstractPylonTile pylonTile)
            {
                pylonTile.dropItems();
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult)
    {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AbstractPylonTile pylonTile) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return new TranslatableComponent(getDescriptionId());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        pylonTile.updateContainer(buffer);
                        return containerFactory().create(i, playerInventory, buffer);
                    }
                };

                if (player.getUUID().equals(pylonTile.getOwner()) || player.hasPermissions(2)) {
                    NetworkHooks.openGui((ServerPlayer) player, containerProvider, pylonTile::updateContainer);
                } else {
                    return InteractionResult.FAIL;
                }
            } else {
                Pylons.LOGGER.error("tile entity not instance of AbstractPylonTile");
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected MutableComponent translate(String key) {
        return new TranslatableComponent(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }
}
