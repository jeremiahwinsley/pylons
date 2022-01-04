package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.permutated.pylons.inventory.container.ExpulsionPylonContainer;
import net.permutated.pylons.util.Constants;

@SuppressWarnings("java:S110") // inheritance required
public class ExpulsionPylonScreen extends AbstractPylonScreen<ExpulsionPylonContainer> {
    public ExpulsionPylonScreen(ExpulsionPylonContainer container, Inventory inv, Component name) {
        super(container, inv, name, Constants.EXPULSION_PYLON);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);

        if (!this.menu.isAllowedDimension()) {
            drawText(matrixStack, translate("wrongDimension").withStyle(ChatFormatting.RED), 42);
        } else if (!this.menu.isAllowedLocation()) {
            drawText(matrixStack, translate("insideWorldSpawn").withStyle(ChatFormatting.RED), 42);
        } else {
            drawText(matrixStack, translate("whitelist"), 42);
        }
    }
}
