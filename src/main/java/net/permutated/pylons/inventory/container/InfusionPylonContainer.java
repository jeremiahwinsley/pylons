package net.permutated.pylons.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.RegistryObject;
import net.permutated.pylons.ModRegistry;

public class InfusionPylonContainer extends AbstractPylonContainer {

    public InfusionPylonContainer(int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        super(ModRegistry.INFUSION_PYLON_CONTAINER.get(), windowId, playerInventory, packetBuffer);

    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.INFUSION_PYLON;
    }
}
