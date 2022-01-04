package net.permutated.pylons.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.inventory.container.AbstractPylonContainer;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.ResourceUtil;
import net.permutated.pylons.util.TranslationKey;

public abstract class AbstractPylonScreen<T extends AbstractPylonContainer> extends AbstractContainerScreen<T> {
    protected final ResourceLocation gui;
    protected Button workButton;
    protected Button rangeButton;

    protected AbstractPylonScreen(T container, Inventory inv, Component name, String pylonType) {
        super(container, inv, name);
        this.gui = ResourceUtil.gui("pylon");
        this.imageWidth = 176;
        this.imageHeight = 172;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // x, y, width, height
        workButton = new Button(this.leftPos + 142, this.height / 2 - 80, 26, 20,
            this.menu.getWorkComponent(), this.menu::sendWorkPacket, this::workButtonTooltip);
        rangeButton = new Button(this.leftPos + 116, this.height / 2 - 80, 26, 20,
            this.menu.getRangeComponent(), this.menu::sendRangePacket, this::rangeButtonTooltip);

        addRenderableWidget(workButton);
        if (this.menu.shouldRenderRange()) {
            addRenderableWidget(rangeButton);
        }
        updateMessages();
    }

    private void workButtonTooltip(Button button, PoseStack poseStack, int p_169460_, int p_169461_) {
        this.renderTooltip(poseStack, translate("toggleWork"), p_169460_, p_169461_);
    }

    private void rangeButtonTooltip(Button button, PoseStack poseStack, int p_169460_, int p_169461_) {
        this.renderTooltip(poseStack, translate("workArea"), p_169460_, p_169461_);
    }

    public void updateMessages() {
        this.workButton.setMessage(this.menu.getWorkComponent());
        this.rangeButton.setMessage(this.menu.getRangeComponent());
    }

    @Override
    protected void containerTick() {
        updateMessages();
        super.containerTick();
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
        drawText(matrixStack, component, 30);
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
