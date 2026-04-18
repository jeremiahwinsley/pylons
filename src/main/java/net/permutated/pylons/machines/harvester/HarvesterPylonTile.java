package net.permutated.pylons.machines.harvester;

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
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.compat.harvest.HarvestCompat;
import net.permutated.pylons.compat.harvest.Harvestable;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.recipe.HarvestingRecipe;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class HarvesterPylonTile extends AbstractPylonTile {
    public HarvesterPylonTile(BlockPos pos, BlockState state) {
        super(ModRegistry.HARVESTER_PYLON_TILE.get(), pos, state);
    }

    @Override
    protected byte[] getRange() {
        return new byte[]{3, 5, 7, 9};
    }

    @Override
    protected boolean isItemValid(ItemResource resource) {
        return resource.getItem() instanceof HoeItem && !resource.is(ModRegistry.HARVESTER_BANNED);
    }

    @Override
    protected boolean canAccessInventory() {
        return ConfigManager.SERVER.harvesterCanBeAutomated.getAsBoolean();
    }

    @Override
    protected boolean canAccessEnergy() {
        return ConfigManager.SERVER.harvesterRequiresPower.getAsBoolean();
    }

    private boolean requiresTool() {
        return !requiresPower() && ConfigManager.SERVER.harvesterRequiresTool.getAsBoolean();
    }

    public static boolean requiresPower() {
        return ConfigManager.SERVER.harvesterRequiresPower.getAsBoolean();
    }

    private int getPowerCost() {
        return ConfigManager.SERVER.harvesterPowerCost.getAsInt();
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
        MISSING_ENERGY,
    }

    @Override
    public void removeChunkloads() {
        //nothing to do
    }

    @Override
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        super.updateContainer(packetBuffer);
        packetBuffer.writeEnum(workStatus);
    }

    @Nullable
    protected BlockCapabilityCache<ResourceHandler<ItemResource>, Direction> outputCache = null;

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

            if (outputCache == null) {
                outputCache = BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel, above, Direction.DOWN);
            }

            ResourceHandler<ItemResource> itemHandler = outputCache.getCapability();
            if (itemHandler == null) {
                workStatus = Status.MISSING_INVENTORY;
                return;
            }

            int hoeSlot = -1;
            if (requiresTool()) {
                for (int i = 0; i < itemStackHandler.size();i++) {
                    if (isItemValid(itemStackHandler.getResource(i))) {
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

                    Optional<HarvestingRecipe> optionalRecipe;
                    BlockState blockState = level.getBlockState(workPos);

                    if (blockState.getBlock() instanceof CropBlock crop) {
                        // make sure crop is fully grown
                        final int currentAge = crop.getAge(blockState);
                        final int maxAge = crop.getMaxAge();

                        if (0 == maxAge || currentAge < maxAge) {
                            continue;
                        }

                        if (!consumeInputs(serverLevel, hoeSlot)) return;

                        // find the crop seed
                        ItemStack seedStack = blockState.getCloneItemStack(level, workPos, true);

                        List<ItemStack> drops = Block.getDrops(blockState, serverLevel, workPos, null);

                        // reset crop age
                        BlockState modified = crop.getStateForAge(0);
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
                    } else if (HarvestCompat.hasCompat(blockState.getBlock())) {
                        Harvestable harvestable = HarvestCompat.getCompat(blockState.getBlock());
                        if (harvestable.isHarvestable(blockState)) {
                            if (!consumeInputs(serverLevel, hoeSlot)) return;

                            Collection<ItemStack> drops = harvestable.harvest(level, workPos, blockState);

                            for (ItemStack drop : drops) {
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
                    } else if ((optionalRecipe = ModRegistry.HARVESTING_REGISTRY.findRecipe(blockState.getBlock())).isPresent()) {
                        HarvestingRecipe recipe = optionalRecipe.get();
                        int age = blockState.getValue(recipe.getAgeProperty());

                        if (age > recipe.getMinAge() && age == recipe.getMaxAge()) {
                            if (!consumeInputs(serverLevel, hoeSlot)) return;

                            ItemStack stack = recipe.getOutput().create();
                            int harvestAge = recipe.getMaxAge() > 1 ? 1 : 0;
                            level.setBlock(workPos, blockState.setValue(recipe.getAgeProperty(), harvestAge), Block.UPDATE_CLIENTS);

                            // try to insert as many drops as possible, discard the rest
                            boolean result = insertItemOrDiscard(itemHandler, stack);
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

    private boolean consumeInputs(ServerLevel serverLevel, int hoeSlot) {
        if (requiresPower()) {
            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = energyStorage.extract(getPowerCost(), transaction);
                if (extracted == getPowerCost()) {
                    transaction.commit();
                } else {
                    workStatus = Status.MISSING_ENERGY;
                    return false;
                }
            }
        } else if (requiresTool()) {
            if (hoeSlot == -1) {
                workStatus = Status.MISSING_TOOL;
                return false;
            } else {
                itemStackHandler.hurtAndBreak(hoeSlot, serverLevel);
            }
        }
        return true;
    }

    /**
     * Iterates all slots in the IItemHandler attempting to insert the stack.
     * Returns true if the stack was successfully inserted, or false if there was anything left over.
     * Discards the remaining stack in either case.
     * @param itemHandler the inventory to insert into
     * @param itemStack the ItemStack to insert
     * @return the result of the attempt
     */
    private boolean insertItemOrDiscard(ResourceHandler<ItemResource> itemHandler, ItemStack itemStack) {
        ItemStack progress = ItemUtil.insertItemReturnRemaining(itemHandler, itemStack, false, null);
        return progress.isEmpty();
    }
}
