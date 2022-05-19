package net.permutated.pylons.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.RegistryObject;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.tile.HarvesterPylonTile;

public class HarvesterPylonContainer extends AbstractPylonContainer {

    private final HarvesterPylonTile.Status workStatus;
    public HarvesterPylonContainer(int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        super(ModRegistry.HARVESTER_PYLON_CONTAINER.get(), windowId, playerInventory, packetBuffer);
        workStatus = packetBuffer.readEnum(HarvesterPylonTile.Status.class);
    }

    @Override
    protected RegistryObject<Block> getBlock() {
        return ModRegistry.HARVESTER_PYLON;
    }

    @Override
    public boolean shouldRenderRange() {
        return true;
    }

    public HarvesterPylonTile.Status getWorkStatus() {
        return workStatus;
    }
}
