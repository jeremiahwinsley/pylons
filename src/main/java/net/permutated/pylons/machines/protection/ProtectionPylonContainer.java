package net.permutated.pylons.machines.protection;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.machines.base.AbstractPylonContainer;

import java.util.function.Supplier;

public class ProtectionPylonContainer extends AbstractPylonContainer {

    public ProtectionPylonContainer(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(ModRegistry.PROTECTION_PYLON_CONTAINER.get(), windowId, playerInventory, packetBuffer);
    }

    @Override
    protected Supplier<Block> getBlock() {
        return ModRegistry.PROTECTION_PYLON;
    }

    @Override
    public boolean shouldRenderRange() {
        return true;
    }
}
