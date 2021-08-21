package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.permutated.pylons.inventory.container.InfusionPylonContainer;
import net.permutated.pylons.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class InfusionPylonScreen extends AbstractPylonScreen<InfusionPylonContainer> {
    public InfusionPylonScreen(InfusionPylonContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name, Constants.INFUSION_PYLON);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        String owner = this.menu.getOwnerName();

        ITextComponent component;
        if (owner != null) {
            component = translate("owner", owner);
        } else {
            component = translate("noOwner").withStyle(TextFormatting.RED);
        }
        drawText(matrixStack, component, 24);
        drawText(matrixStack, translate("effects"), 36);
    }
}
