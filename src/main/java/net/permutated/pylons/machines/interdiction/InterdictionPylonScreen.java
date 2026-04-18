package net.permutated.pylons.machines.interdiction;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.machines.base.AbstractPylonScreen;

@SuppressWarnings("java:S110") // inheritance required
public class InterdictionPylonScreen extends AbstractPylonScreen<InterdictionPylonContainer> {
    public InterdictionPylonScreen(InterdictionPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        drawText(graphics, translate("blockedMobs"), 42);
    }
}
