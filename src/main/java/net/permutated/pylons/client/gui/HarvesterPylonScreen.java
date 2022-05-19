package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.inventory.container.HarvesterPylonContainer;
import net.permutated.pylons.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class HarvesterPylonScreen extends AbstractPylonScreen<HarvesterPylonContainer> {
    public HarvesterPylonScreen(HarvesterPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name, Constants.HARVESTER_PYLON);
    }

    @Override
    protected void rangeButtonTooltip(Button button, PoseStack poseStack, int p_169460_, int p_169461_) {
        this.renderTooltip(poseStack, translate("workAreaBlocks"), p_169460_, p_169461_);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        switch (this.menu.getWorkStatus()) {
            case MISSING_INVENTORY:
                drawText(matrixStack, translate("inventoryMissing").withStyle(ChatFormatting.RED), 42);
                break;
            case MISSING_TOOL:
                drawText(matrixStack, translate("toolMissing").withStyle(ChatFormatting.RED), 42);
                break;
            case INVENTORY_FULL:
                drawText(matrixStack, translate("inventoryFull").withStyle(ChatFormatting.RED), 42);
                break;
            case WORKING:
                drawText(matrixStack, translate("working").withStyle(ChatFormatting.DARK_GREEN), 42);
                break;
        }
    }
}
