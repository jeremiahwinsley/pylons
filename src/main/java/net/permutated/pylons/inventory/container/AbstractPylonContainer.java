package net.permutated.pylons.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.permutated.pylons.network.NetworkDispatcher;
import net.permutated.pylons.network.PacketButtonClicked;
import net.permutated.pylons.tile.AbstractPylonTile;

import javax.annotation.Nullable;

public abstract class AbstractPylonContainer extends Container {

    @Nullable // should only be accessed from server
    private final AbstractPylonTile tileEntity;
    protected final String ownerName;
    protected final BlockPos blockPos;

    protected AbstractPylonContainer(@Nullable ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        super(containerType, windowId);

        blockPos = packetBuffer.readBlockPos();
        World world = playerInventory.player.getCommandSenderWorld();

        tileEntity = (AbstractPylonTile) world.getBlockEntity(blockPos);
        IItemHandler wrappedInventory = new InvWrapper(playerInventory);

        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                for (int slot = 0; slot < AbstractPylonTile.SLOTS; slot++) {
                    addSlot(new SlotItemHandler(handler, slot, 8 + slot * 18, 54));
                }
            });
        }

        int nameLength = packetBuffer.readInt();
        ownerName = packetBuffer.readUtf(nameLength);

        registerPlayerSlots(wrappedInventory);
    }

    protected abstract RegistryObject<Block> getBlock();

    protected int getInventorySize() {
        return AbstractPylonTile.SLOTS;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public ITextComponent getWorkComponent() {
        boolean shouldWork = tileEntity != null && tileEntity.shouldWork();
        return new StringTextComponent(shouldWork ? "On" : "Off");
    }

    @SuppressWarnings("java:S1172") // parameter required
    public void sendWorkPacket(Button button) {
        NetworkDispatcher.INSTANCE.sendToServer(new PacketButtonClicked(PacketButtonClicked.ButtonType.WORK, blockPos));
    }

    public boolean shouldRenderRange() {
        return false;
    }

    public ITextComponent getRangeComponent() {
        int range = tileEntity != null ? tileEntity.getSelectedRange() : 0;
        return new StringTextComponent(String.format("%dx%d", range, range));
    }

    @SuppressWarnings("java:S1172") // parameter required
    public void sendRangePacket(Button button) {
        NetworkDispatcher.INSTANCE.sendToServer(new PacketButtonClicked(PacketButtonClicked.ButtonType.RANGE, blockPos));
    }


    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        if (tileEntity != null) {
            World world = tileEntity.getLevel();
            if (world != null) {
                IWorldPosCallable callable = IWorldPosCallable.create(world, tileEntity.getBlockPos());
                return stillValid(callable, playerEntity, getBlock().get());
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
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
}
