package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.util.ResourceUtil;
import net.permutated.pylons.util.TranslationKey;

import java.util.Objects;

public  abstract class AbstractPylonScreen<T extends AbstractPylonContainer> extends ContainerScreen<T> {
    protected final ResourceLocation gui;

    protected AbstractPylonScreen(T container, PlayerInventory inv, ITextComponent name, String pylonType) {
        super(container, inv, name);
        this.gui = ResourceUtil.gui("pylon");
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Objects.requireNonNull(this.minecraft).getTextureManager().bind(gui);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void drawText(MatrixStack stack, ITextComponent component, int yPos) {
        this.font.draw(stack, component, 8, yPos, 4210752);
    }

    protected TranslationTextComponent translate(String key) {
        return new TranslationTextComponent(TranslationKey.gui(key));
    }

    protected TranslationTextComponent translate(String key, Object... values) {
        return new TranslationTextComponent(TranslationKey.gui(key), values);
    }
}
