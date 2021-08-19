package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.permutated.pylons.inventory.container.ExpulsionPylonContainer;
import net.permutated.pylons.util.Constants;

public class ExpulsionPylonScreen extends AbstractPylonScreen<ExpulsionPylonContainer> {
    public ExpulsionPylonScreen(ExpulsionPylonContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name, Constants.EXPULSION_PYLON);
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
        drawText(matrixStack, translate("whitelist"), 36);
    }
}
