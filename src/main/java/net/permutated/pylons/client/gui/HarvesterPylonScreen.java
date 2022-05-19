package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.permutated.pylons.inventory.container.HarvesterPylonContainer;
import net.permutated.pylons.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class HarvesterPylonScreen extends AbstractPylonScreen<HarvesterPylonContainer> {
    public HarvesterPylonScreen(HarvesterPylonContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name, Constants.HARVESTER_PYLON);
    }

    @Override
    protected void rangeButtonTooltip(Button button, MatrixStack poseStack, int p_169460_, int p_169461_) {
        this.renderTooltip(poseStack, translate("workAreaBlocks"), p_169460_, p_169461_);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        switch (this.menu.getWorkStatus()) {
            case MISSING_INVENTORY:
                drawText(matrixStack, translate("inventoryMissing").withStyle(TextFormatting.RED), 42);
                break;
            case MISSING_TOOL:
                drawText(matrixStack, translate("toolMissing").withStyle(TextFormatting.RED), 42);
                break;
            case INVENTORY_FULL:
                drawText(matrixStack, translate("inventoryFull").withStyle(TextFormatting.RED), 42);
                break;
            case WORKING:
                drawText(matrixStack, translate("working").withStyle(TextFormatting.DARK_GREEN), 42);
                break;
        }
    }
}
