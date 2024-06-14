package net.permutated.pylons.machines.expulsion;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.machines.base.AbstractPylonContainer;

import java.util.function.Supplier;

public class ExpulsionPylonContainer extends AbstractPylonContainer {

    private final boolean allowedDimension;
    private final boolean allowedLocation;

    public ExpulsionPylonContainer(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), windowId, playerInventory, packetBuffer);
        allowedDimension = packetBuffer.readBoolean();
        allowedLocation = packetBuffer.readBoolean();
    }

    @Override
    protected Supplier<Block> getBlock() {
        return ModRegistry.EXPULSION_PYLON;
    }

    public boolean isAllowedDimension() {
        return allowedDimension;
    }

    public boolean isAllowedLocation() {
        return allowedLocation;
    }

    @Override
    public boolean shouldRenderRange() {
        return true;
    }
}
