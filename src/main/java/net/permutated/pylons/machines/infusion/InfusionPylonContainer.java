package net.permutated.pylons.machines.infusion;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.machines.base.AbstractPylonContainer;

import java.util.function.Supplier;

public class InfusionPylonContainer extends AbstractPylonContainer {

    public InfusionPylonContainer(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(ModRegistry.INFUSION_PYLON_CONTAINER.get(), windowId, playerInventory, packetBuffer);

    }

    @Override
    protected Supplier<Block> getBlock() {
        return ModRegistry.INFUSION_PYLON;
    }
}
