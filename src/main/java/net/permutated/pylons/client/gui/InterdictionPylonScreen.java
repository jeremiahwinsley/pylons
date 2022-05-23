package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.permutated.pylons.inventory.container.InterdictionPylonContainer;
import net.permutated.pylons.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class InterdictionPylonScreen extends AbstractPylonScreen<InterdictionPylonContainer> {
    public InterdictionPylonScreen(InterdictionPylonContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name, Constants.INTERDICTION_PYLON);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        drawText(matrixStack, translate("blockedMobs"), 42);
    }
}
