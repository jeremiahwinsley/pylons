package net.permutated.pylons.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.permutated.pylons.tile.AbstractPylonTile;

import javax.annotation.Nullable;

public abstract class AbstractPylonContainer extends Container {

    protected final AbstractPylonTile tileEntity;

    protected AbstractPylonContainer(@Nullable ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        super(containerType, windowId);

        BlockPos pos = packetBuffer.readBlockPos();
        World world = playerInventory.player.getCommandSenderWorld();

        tileEntity = (AbstractPylonTile) world.getBlockEntity(pos);
        IItemHandler wrappedInventory = new InvWrapper(playerInventory);

        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                for (int slot = 0; slot < AbstractPylonTile.SLOTS; slot++) {
                    addSlot(new SlotItemHandler(handler, slot, 8 + slot * 18, 48));
                }
            });
        }

        registerPlayerSlots(wrappedInventory);
    }

    protected abstract RegistryObject<Block> getBlock();

    @Nullable
    public String getOwnerName() {
        return tileEntity.getOwnerName();
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        World world = tileEntity.getLevel();

        if (world != null) {
            IWorldPosCallable callable = IWorldPosCallable.create(world, tileEntity.getBlockPos());
            return stillValid(callable, playerEntity, getBlock().get());
        } else {
            return false;
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        int inventorySize = this.tileEntity.getInventorySize();

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
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new SlotItemHandler(wrappedInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlot(new SlotItemHandler(wrappedInventory, i, 8 + i * 18, 142));
        }
    }
}
