package net.permutated.pylons.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.tile.ExpulsionPylonTile;

public class ExpulsionPylonContainer extends AbstractPylonContainer {

    public ExpulsionPylonContainer(int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        super(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), windowId);

        BlockPos pos = packetBuffer.readBlockPos();
        World world = playerInventory.player.getCommandSenderWorld();

        tileEntity = (ExpulsionPylonTile) world.getBlockEntity(pos);
        IItemHandler wrappedInventory = new InvWrapper(playerInventory);

        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                for (int slot = 0; slot < ExpulsionPylonTile.SLOTS; slot++) {
                    addSlot(new SlotItemHandler(handler, slot, 8 + slot * 18, 48));
                }
            });
        }

        registerPlayerSlots(wrappedInventory);
    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.EXPULSION_PYLON;
    }
}
