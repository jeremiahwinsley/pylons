package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.permutated.pylons.inventory.container.InfusionPylonContainer;
import net.permutated.pylons.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class InfusionPylonScreen extends AbstractPylonScreen<InfusionPylonContainer> {
    public InfusionPylonScreen(InfusionPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name, Constants.INFUSION_PYLON);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        drawText(matrixStack, translate("effects"), 42);
    }
}
