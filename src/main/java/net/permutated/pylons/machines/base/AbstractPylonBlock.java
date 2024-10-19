package net.permutated.pylons.machines.base;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;

public abstract class AbstractPylonBlock extends Block implements EntityBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
        Block.box(1.0D, 3.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    );

    protected AbstractPylonBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(INDESTRUCTIBLE, 1200F));
        this.registerDefaultState(this.defaultBlockState()
            .setValue(ENABLED, Boolean.TRUE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated
    public VoxelShape getShape(BlockState blockState, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public abstract IContainerFactory<AbstractPylonContainer> containerFactory();

    @SuppressWarnings("java:S1452") // wildcard required here
    public abstract BlockEntityType<? extends AbstractPylonTile> getTileType();


    /**
     * Check if player is either the pylon owner or has OP
     *
     * @param blockEntity the pylon tile
     * @param player      the player
     * @return the result
     */
    public static boolean canAccessPylon(@Nullable BlockEntity blockEntity, Player player) {
        return (blockEntity instanceof AbstractPylonTile tile && tile.canAccess(player));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == getTileType() ? AbstractPylonTile::tick : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
        if (!level.isClientSide && entity instanceof Player && !(entity instanceof FakePlayer)) {
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            if (tileEntity instanceof AbstractPylonTile pylonTile) {
                pylonTile.setOwner(entity.getUUID());
            }
        }
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState blockState) {
        if (world.getBlockEntity(blockPos) instanceof AbstractPylonTile pylonTile) {
            pylonTile.removeChunkloads();
            pylonTile.dropItems();
        }

        super.destroy(world, blockPos, blockState);
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos blockPos) {
        BlockEntity blockEntity = getter.getBlockEntity(blockPos);
        if (canAccessPylon(blockEntity, player)) {
            int i = EventHooks.doPlayerHarvestCheck(player, state, getter, blockPos) ? 30 : 100;
            return player.getDigSpeed(state, blockPos) / 2.0F / i;
        }
        return 0.0F;
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof AbstractPylonTile pylonTile) {
                pylonTile.removeChunkloads();
                pylonTile.dropItems();
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!newState.is(state.getBlock())) {
            this.checkPoweredState(level, pos, state);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        this.checkPoweredState(level, pos, state);
    }

    //TODO avoid chunk loading?
    private void checkPoweredState(Level level, BlockPos pos, BlockState state) {
        boolean flag = !level.hasNeighborSignal(pos);
        if (!Boolean.valueOf(flag).equals((state.getValue(ENABLED)))) {
            level.setBlock(pos, state.setValue(ENABLED, flag), Block.UPDATE_ALL);
        }

    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockRayTraceResult) {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AbstractPylonTile pylonTile) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable(getDescriptionId());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(),
                            player.registryAccess(), ConnectionType.NEOFORGE);
                        pylonTile.updateContainer(buffer);
                        return containerFactory().create(i, playerInventory, buffer);
                    }
                };

                if (canAccessPylon(tileEntity, player)) {
                    player.openMenu(containerProvider, pylonTile::updateContainer);
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
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }
}
