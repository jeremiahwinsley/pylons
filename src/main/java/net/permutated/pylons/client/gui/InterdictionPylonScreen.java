package net.permutated.pylons.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.inventory.container.InterdictionPylonContainer;

@SuppressWarnings("java:S110") // inheritance required
public class InterdictionPylonScreen extends AbstractPylonScreen<InterdictionPylonContainer> {
    public InterdictionPylonScreen(InterdictionPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        drawText(graphics, translate("blockedMobs"), 42);
    }
}
