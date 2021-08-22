package net.permutated.pylons.inventory.container;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.RegistryObject;
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

    public boolean isAllowedDimension() {
        if (tileEntity instanceof ExpulsionPylonTile) {
            ExpulsionPylonTile pylonTile = (ExpulsionPylonTile) tileEntity;
            return pylonTile.isAllowedDimension();
        }
        return false;
    }
}
