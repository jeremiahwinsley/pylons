package net.permutated.pylons.machines.harvester;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.machines.base.AbstractPylonScreen;

@SuppressWarnings("java:S110") // inheritance required
public class HarvesterPylonScreen extends AbstractPylonScreen<HarvesterPylonContainer> {
    public HarvesterPylonScreen(HarvesterPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected Tooltip rangeButtonTooltip() {
        return Tooltip.create(translate("workAreaBlocks"));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        switch (this.menu.getWorkStatus()) {
            case MISSING_INVENTORY:
                drawText(graphics, translate("inventoryMissing").withStyle(ChatFormatting.RED), 42);
                break;
            case MISSING_TOOL:
                drawText(graphics, translate("toolMissing").withStyle(ChatFormatting.RED), 42);
                break;
            case INVENTORY_FULL:
                drawText(graphics, translate("inventoryFull").withStyle(ChatFormatting.RED), 42);
                break;
            case WORKING:
                drawText(graphics, translate("working").withStyle(ChatFormatting.DARK_GREEN), 42);
                break;
        }
    }
}
