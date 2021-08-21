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
        super(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), windowId, playerInventory, packetBuffer);
    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.EXPULSION_PYLON;
    }
}
