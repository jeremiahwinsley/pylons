package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.permutated.pylons.inventory.container.ExpulsionPylonContainer;
import net.permutated.pylons.util.Constants;

import java.util.UUID;

public class ExpulsionPylonScreen extends AbstractPylonScreen<ExpulsionPylonContainer> {
    private final UUID owner = UUID.randomUUID();

    public ExpulsionPylonScreen(ExpulsionPylonContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name, Constants.EXPULSION_PYLON);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        this.font.draw(matrixStack, new TranslationTextComponent("gui.pylons.owner", owner), 8, 24, 4210752);
        this.font.draw(matrixStack, new TranslationTextComponent("gui.pylons.whitelist"), 8, 36, 4210752);
    }
}
