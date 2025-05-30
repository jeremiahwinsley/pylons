package net.permutated.pylons.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.compat.teams.TeamCompat;
import net.permutated.pylons.util.ChunkManager;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractPylonTile extends BlockEntity {

    protected AbstractPylonTile(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
        int buffer = ConfigManager.SERVER.harvesterPowerBuffer.getAsInt();
        energyStorage = new PylonEnergyStorage(this::setChanged, buffer, buffer);
    }

    public static final int SLOTS = 9;
    public static final UUID NONE = new UUID(0,0);

    protected final PylonEnergyStorage energyStorage;

    protected final ItemStackHandler itemStackHandler = new PylonItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return AbstractPylonTile.this.isItemValid(stack);
        }
    };

    protected abstract boolean isItemValid(ItemStack stack);

    protected boolean canAccessInventory() {
        return true;
    }

    protected boolean canAccessEnergy() {
        return false;
    }

    /**
     * Helper method for registering item capabilities. Called by Pylons#onRegisterCapabilitiesEvent
     * @param event the registration event
     * @param blockEntityType the block entity being registered
     */
    public static void registerItemCapability(RegisterCapabilitiesEvent event, BlockEntityType<? extends AbstractPylonTile> blockEntityType) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType,
            (pylon, side) -> pylon.canAccessInventory() ? pylon.itemStackHandler : null);
    }

    /**
     * Helper method for registering energy capabilities. Called by Pylons#onRegisterCapabilitiesEvent.
     * @param event the registration event
     * @param blockEntityType the block entity being registered
     */
    public static void registerEnergyCapability(RegisterCapabilitiesEvent event, BlockEntityType<? extends AbstractPylonTile> blockEntityType) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, blockEntityType,
            (pylon, side) -> pylon.canAccessEnergy() ? pylon.energyStorage : null);
    }

    public void dropItems() {
        AbstractPylonTile.dropItems(level, worldPosition, itemStackHandler);
    }

    public void removeChunkloads() {
        if (owner != null && level instanceof ServerLevel serverLevel) {
            ChunkManager.unloadChunk(owner, serverLevel, getBlockPos());
        }
    }

    protected UUID owner = NONE;

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.setChanged();
    }

    public boolean hasTeamAccess(Player player) {
        return Boolean.TRUE.equals(ConfigManager.SERVER.teamSupportEnabled.get())
            && TeamCompat.getInstance().arePlayersInSameTeam(owner, player.getUUID());
    }

    public boolean canAccess(Player player) {
        return Objects.equals(player.getUUID(), owner) || owner == NONE || player.hasPermissions(2) || hasTeamAccess(player);
    }

    private long lastTicked = 0L;

    public boolean canTick(final int every) {
        long gameTime = level != null ? level.getGameTime() : 0L;
        if (offset(gameTime) % every == 0 && gameTime != lastTicked && shouldWork()) {
            lastTicked = gameTime;
            return true;
        } else {
            return false;
        }
    }

    int offset = 0;
    /**
     * Add a random offset between 0 and 19 ticks.
     * This is generated once per block entity on the first tick.
     * @param gameTime the current game time
     * @return the tick delay with the saved offset
     */
    protected long offset(final long gameTime) {
        if (offset == 0) offset += ThreadLocalRandom.current().nextInt(0, 20);
        return gameTime + offset;
    }

    public abstract void tick();

    @SuppressWarnings("java:S1172") // unused arguments are required
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (blockEntity instanceof AbstractPylonTile pylonTile && pylonTile.shouldWork()) {
            pylonTile.tick();
        }
    }

    protected static void dropItems(@Nullable Level world, BlockPos pos, IItemHandler itemHandler) {
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemstack = itemHandler.getStackInSlot(i);

            if (itemstack.getCount() > 0 && world != null) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
            }
        }
    }

    public String getOwnerName() {
        String lastKnown = owner == NONE ? null : UsernameCache.getLastKnownUsername(owner);
        return Objects.toString(lastKnown, Constants.UNKNOWN);
    }

    /**
     * Serialize data to be sent to the GUI on the client.
     * <p>
     * Overrides MUST call the super method first to ensure correct deserialization.
     *
     * @param packetBuffer the packet ready to be filled
     */
    public void updateContainer(FriendlyByteBuf packetBuffer) {
        String username = getOwnerName();

        packetBuffer.writeBlockPos(worldPosition);
        packetBuffer.writeInt(shouldWork() ? 1 : 0);
        packetBuffer.writeInt(getSelectedRange());
        packetBuffer.writeInt(username.length());
        packetBuffer.writeUtf(username);
    }

    // Save TE data to disk
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(Constants.NBT.INV, itemStackHandler.serializeNBT(registries));
        tag.put(Constants.NBT.ENERGY, energyStorage.serializeNBT(registries));
        tag.put(Constants.NBT.RANGE, range.serializeNBT());
        tag.putUUID(Constants.NBT.OWNER, owner);
    }

    // Load TE data from disk
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemStackHandler.deserializeNBT(registries, tag.getCompound(Constants.NBT.INV));
        energyStorage.deserializeNBT(registries, tag.get(Constants.NBT.ENERGY));
        range.deserializeNBT(tag.getCompound(Constants.NBT.RANGE));
        owner = tag.getUUID(Constants.NBT.OWNER);
        super.loadAdditional(tag, registries);
    }

    // Called whenever a client loads a new chunk
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putUUID(Constants.NBT.OWNER, owner);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        if (!tag.isEmpty()) {
            owner = tag.getUUID(Constants.NBT.OWNER);
            super.handleUpdateTag(tag, lookupProvider);
        }
    }

    // Called whenever a block update happens on the client
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public class PylonItemStackHandler extends ItemStackHandler {
        public PylonItemStackHandler(int size) {
            super(size);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    }

    public boolean shouldWork() {
        return getBlockState().getValue(AbstractPylonBlock.ENABLED);
    }

    public void handleWorkPacket() {
        if (this.level != null) {
            boolean shouldWork = !shouldWork();
            level.setBlock(getBlockPos(), getBlockState().setValue(AbstractPylonBlock.ENABLED, shouldWork), Block.UPDATE_ALL);
            if (!shouldWork) removeChunkloads();
        }
    }

    protected final Range range = new Range(getRange());

    protected byte[] getRange() {
        return new byte[]{1};
    }

    public int getSelectedRange() {
        return range.get();
    }

    public void handleRangePacket() {
        if (getRange().length > 1 && this.level != null) {
            this.range.next();
            this.setChanged();
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        }
    }
}
