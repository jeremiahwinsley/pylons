package net.permutated.pylons.machines.base;

import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.network.PacketButtonClicked;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class AbstractPylonContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess containerLevelAccess;
    protected final DataHolder dataHolder;
    protected final String ownerName;
    protected final BlockPos blockPos;

    protected AbstractPylonContainer(@Nullable MenuType<?> containerType, int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(containerType, windowId);

        blockPos = packetBuffer.readBlockPos();
        Level level = playerInventory.player.getCommandSenderWorld();
        if (level instanceof ServerLevel serverLevel) { // server
            this.containerLevelAccess = ContainerLevelAccess.create(level, blockPos);
            BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
            if (blockEntity instanceof AbstractPylonTile tile) {
                dataHolder = new DataHolderServer(tile);
                registerHandlerSlots(tile.itemStackHandler);
            } else {
                Pylons.LOGGER.error("Did not find matching block entity for pos: {}", blockPos);
                dataHolder = new DataHolderClient();
            }
        } else { // client
            containerLevelAccess = ContainerLevelAccess.NULL;
            dataHolder = new DataHolderClient();
            registerHandlerSlots(new ItemStackHandler(AbstractPylonTile.SLOTS));
        }
        registerPlayerSlots(new InvWrapper(playerInventory));
        registerDataSlots();

        dataHolder.setEnabled(packetBuffer.readInt());
        dataHolder.setRange(packetBuffer.readInt());

        int nameLength = packetBuffer.readInt();
        ownerName = packetBuffer.readUtf(nameLength);

    }

    protected abstract Supplier<Block> getBlock();

    protected int getInventorySize() {
        return AbstractPylonTile.SLOTS;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Component getWorkComponent() {
        boolean shouldWork = dataHolder.getEnabled() == 1;
        return Component.literal(shouldWork ? "On" : "Off");
    }

    @SuppressWarnings("java:S1172") // parameter required
    public void sendWorkPacket(Button button) {
        PacketDistributor.sendToServer(new PacketButtonClicked(PacketButtonClicked.ButtonType.WORK, blockPos));
    }

    public boolean shouldRenderRange() {
        return false;
    }

    public Component getRangeComponent() {
        int range = dataHolder.getRange();
        return Component.literal(String.format("%dx%d", range, range));
    }

    @SuppressWarnings("java:S1172") // parameter required
    public void sendRangePacket(Button button) {
        PacketDistributor.sendToServer(new PacketButtonClicked(PacketButtonClicked.ButtonType.RANGE, blockPos));
    }

    @Override
    public boolean stillValid(Player playerEntity) {
        return stillValid(containerLevelAccess, playerEntity, getBlock().get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        int inventorySize = getInventorySize();

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < inventorySize) {
                if (!this.moveItemStackTo(stack, inventorySize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public void registerHandlerSlots(IItemHandler inventory) {
        for (int slot = 0; slot < AbstractPylonTile.SLOTS; slot++) {
            addSlot(new SlotItemHandler(inventory, slot, 8 + slot * 18, 54));
        }
    }

    public void registerPlayerSlots(IItemHandler wrappedInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(wrappedInventory, j + i * 9 + 9, 8 + j * 18, 90 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(wrappedInventory, i, 8 + i * 18, 148));
        }
    }

    public void registerDataSlots() {
        addDataSlot(dataHolder::getRange, dataHolder::setRange);
        addDataSlot(dataHolder::getEnabled, dataHolder::setEnabled);
    }

    private void addDataSlot(IntSupplier getter, IntConsumer setter) {
        addDataSlot(new LambdaDataSlot(getter, setter));
    }

    /**
     * Based on <a href="https://github.com/Shadows-of-Fire/Placebo/blob/b104501c18e2f6432c843944a8106d07cab825cf/src/main/java/shadows/placebo/container/EasyContainerData.java">Placebo</a>
     */
    static class LambdaDataSlot extends DataSlot {

        private final IntSupplier getter;
        private final IntConsumer setter;

        public LambdaDataSlot(IntSupplier getter, IntConsumer setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int get() {
            return this.getter.getAsInt();
        }

        @Override
        public void set(int pValue) {
            this.setter.accept(pValue);
        }

    }
}
