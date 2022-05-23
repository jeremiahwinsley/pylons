package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.inventory.container.InterdictionPylonContainer;
import net.permutated.pylons.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class InterdictionPylonScreen extends AbstractPylonScreen<InterdictionPylonContainer> {
    public InterdictionPylonScreen(InterdictionPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name, Constants.INTERDICTION_PYLON);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        drawText(matrixStack, translate("blockedMobs"), 42);
    }
}
