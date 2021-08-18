package net.permutated.pylons.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.permutated.pylons.tile.AbstractPylonTile;


import javax.annotation.Nullable;

public abstract class AbstractPylonContainer extends Container {

    protected AbstractPylonTile tileEntity;

    protected AbstractPylonContainer(@Nullable ContainerType<?> containerType, int windowId) {
        super(containerType, windowId);
    }

    protected abstract RegistryObject<Block> getBlock();

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
