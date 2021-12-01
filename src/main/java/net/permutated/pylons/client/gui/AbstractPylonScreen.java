package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.ResourceUtil;
import net.permutated.pylons.util.TranslationKey;

public  abstract class AbstractPylonScreen<T extends AbstractPylonContainer> extends AbstractContainerScreen<T> {
    protected final ResourceLocation gui;

    protected AbstractPylonScreen(T container, Inventory inv, Component name, String pylonType) {
        super(container, inv, name);
        this.gui = ResourceUtil.gui("pylon");
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, gui);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        String owner = this.menu.getOwnerName();

        Component component;
        if (owner.equals(Constants.UNKNOWN)) {
            component = translate("noOwner").withStyle(ChatFormatting.RED);
        } else {
            component = translate("owner", owner);
        }
        drawText(matrixStack, component, 24);
    }

    protected void drawText(PoseStack stack, Component component, int yPos) {
        this.font.draw(stack, component, 8, yPos, 4210752);
    }

    protected TranslatableComponent translate(String key) {
        return new TranslatableComponent(TranslationKey.gui(key));
    }

    protected TranslatableComponent translate(String key, Object... values) {
        return new TranslatableComponent(TranslationKey.gui(key), values);
    }
}
