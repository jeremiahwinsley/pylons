package net.permutated.pylons.machines.infusion;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.machines.base.AbstractPylonScreen;

@SuppressWarnings("java:S110") // inheritance required
public class InfusionPylonScreen extends AbstractPylonScreen<InfusionPylonContainer> {
    public InfusionPylonScreen(InfusionPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        drawText(graphics, translate("effects"), 42);
    }
}
