package net.permutated.pylons.machines.expulsion;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.machines.base.AbstractPylonScreen;

@SuppressWarnings("java:S110") // inheritance required
public class ExpulsionPylonScreen extends AbstractPylonScreen<ExpulsionPylonContainer> {
    public ExpulsionPylonScreen(ExpulsionPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        if (!this.menu.isAllowedDimension()) {
            drawText(graphics, translate("wrongDimension").withStyle(ChatFormatting.RED), 42);
        } else if (!this.menu.isAllowedLocation()) {
            drawText(graphics, translate("insideWorldSpawn").withStyle(ChatFormatting.RED), 42);
        } else {
            drawText(graphics, translate("whitelist"), 42);
        }
    }
}
