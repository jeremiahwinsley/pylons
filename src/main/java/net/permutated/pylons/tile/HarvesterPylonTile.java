package net.permutated.pylons.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.block.HarvesterPylonBlock;

import java.util.Collections;
import java.util.List;

public class HarvesterPylonTile extends AbstractPylonTile {
    public HarvesterPylonTile(BlockPos pos, BlockState state) {
        super(ModRegistry.HARVESTER_PYLON_TILE.get(), pos, state);
    }

    @Override
    protected byte[] getRange() {
        return new byte[]{3, 5, 7, 9};
    }
    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
    }

    @Override
    protected boolean canAccessInventory() {
        return ConfigManager.SERVER.harvesterCanBeAutomated.get();
    }

    private boolean requiresTool() {
        return Boolean.TRUE.equals(ConfigManager.SERVER.harvesterRequiresTool.get());
    }

    private int getWorkDelay() {
        return ConfigManager.SERVER.harvesterWorkDelay.get();
    }

    Status workStatus = Status.NONE;

    public enum Status {
        NONE,
        WORKING,
        MISSING_TOOL,
        MISSING_INVENTORY,
        INVENTORY_FULL,
        UPDATE_ERROR,
    }



    @Override
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        super.updateContainer(packetBuffer);
        packetBuffer.writeEnum(workStatus);
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(getWorkDelay())) {

            // ensure that block above is a valid inventory, and get an IItemHandler
            BlockPos above = getBlockPos().above();
            BlockEntity target = level.getBlockEntity(above);
            if (target == null) {
                workStatus = Status.MISSING_INVENTORY;
                return;
            }
            IItemHandler itemHandler = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN)
                .resolve()
                .orElse(null);
            if (itemHandler == null) {
                workStatus = Status.MISSING_INVENTORY;
                return;
            }

            int hoeSlot = -1;
            if (requiresTool()) {
                for (int i = 0; i < itemStackHandler.getSlots();i++) {
                    if (isItemValid(itemStackHandler.getStackInSlot(i))) {
                        hoeSlot = i;
                        break;
                    }
                }
                if (hoeSlot == -1) {
                    workStatus = Status.MISSING_TOOL;
                    return;
                }
            }

            boolean waterlogged = this.getBlockState().getValue(HarvesterPylonBlock.WATERLOGGED);
            int workY = waterlogged ? above.getY() : getBlockPos().getY();

            // convert range to radius and iterate over every loaded block
            int radius = (range.get() - 1) / 2;

            int minX = above.getX() - radius;
            int minZ = above.getZ() - radius;
            int maxX = above.getX() + radius;
            int maxZ = above.getZ() + radius;
            for (int x = minX;x <= maxX;x++) {
                for (int z = minZ;z <= maxZ;z++) {
                    BlockPos workPos = new BlockPos(x, workY, z);
                    if (!level.isLoaded(workPos)) {
                        continue;
                    }

                    BlockState blockState = level.getBlockState(workPos);

                    if (blockState.getBlock() instanceof CropBlock crop) {
                        // make sure crop is fully grown
                        IntegerProperty ageProperty = crop.getAgeProperty();
                        final int currentAge = blockState.getValue(ageProperty);
                        final int minAge = Collections.min(ageProperty.getPossibleValues());
                        final int maxAge = Collections.max(ageProperty.getPossibleValues());

                        if (minAge == maxAge || currentAge < maxAge) {
                            continue;
                        }

                        if (requiresTool()) {
                            if (hoeSlot == -1) {
                                workStatus = Status.MISSING_TOOL;
                                return;
                            } else {
                                ItemStack replace = itemStackHandler.getStackInSlot(hoeSlot).copy();
                                if (replace.hurt(1, level.getRandom(), null)) {
                                    replace.shrink(1);
                                }
                                itemStackHandler.setStackInSlot(hoeSlot, replace);
                            }
                        }

                        // find the crop seed
                        ItemStack seedStack = crop.getCloneItemStack(level, workPos, blockState);

                        List<ItemStack> drops = Block.getDrops(blockState, serverLevel, workPos, null);

                        // reset crop age
                        BlockState modified = blockState.setValue(ageProperty, minAge);
                        boolean updated = level.setBlockAndUpdate(workPos, modified);
                        if (!updated) {
                            workStatus = Status.UPDATE_ERROR;
                            Pylons.LOGGER.error("Failed to reset crop age for position: {}", workPos);
                            return;
                        }

                        for (ItemStack drop : drops) {
                            // remove 1 crop seed
                            if (!seedStack.isEmpty() && drop.getItem() == seedStack.getItem()) {
                                drop.shrink(1);
                            }

                            if (drop.isEmpty()) {
                                continue;
                            }

                            // try to insert as many drops as possible, discard the rest
                            boolean result = insertItemOrDiscard(itemHandler, drop);
                            if (!result) {
                                workStatus = Status.INVENTORY_FULL;
                                return;
                            }
                        }
                    }
                }
            }
            workStatus = Status.WORKING;
        }
    }

    /**
     * Iterates all slots in the IItemHandler attempting to insert the stack.
     * Returns true if the stack was successfully inserted, or false if there was anything left over.
     * Discards the remaining stack in either case.
     * @param itemHandler the inventory to insert into
     * @param itemStack the ItemStack to insert
     * @return the result of the attempt
     */
    private boolean insertItemOrDiscard(IItemHandler itemHandler, ItemStack itemStack) {
        ItemStack progress = itemStack;
        for (int slot = 0;slot < itemHandler.getSlots();slot++) {
            progress = itemHandler.insertItem(slot, progress, false);
            if (progress.isEmpty()) {
                break;
            }
        }
        return progress.isEmpty();
    }
}
