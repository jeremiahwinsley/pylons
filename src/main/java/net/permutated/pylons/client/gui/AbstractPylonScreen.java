package net.permutated.pylons.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

    protected AbstractPylonScreen(T container, Inventory inv, Component name) {
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
        workButton = Button.builder(this.menu.getWorkComponent(), this.menu::sendWorkPacket)
            .pos(this.leftPos + 142, this.height / 2 - 80)
            .size(26, 20)
            .tooltip(workButtonTooltip())
            .build();

        rangeButton = Button.builder(this.menu.getRangeComponent(), this.menu::sendRangePacket)
            .pos(this.leftPos + 116, this.height / 2 - 80)
            .size(26, 20)
            .tooltip(rangeButtonTooltip())
            .build();

        addRenderableWidget(workButton);
        if (this.menu.shouldRenderRange()) {
            addRenderableWidget(rangeButton);
        }
        updateMessages();
    }

    protected Tooltip workButtonTooltip() {
        return Tooltip.create(translate("toggleWork"));
    }

    protected Tooltip rangeButtonTooltip() {
        return Tooltip.create(translate("workArea"));
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(gui, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        String owner = this.menu.getOwnerName();

        Component component;
        if (owner.equals(Constants.UNKNOWN)) {
            component = translate("noOwner").withStyle(ChatFormatting.RED);
        } else {
            component = translate("owner", owner);
        }
        drawText(graphics, component, 30);
    }

    protected void drawText(GuiGraphics graphics, Component component, int yPos) {
        graphics.drawString(this.font, component, 8, yPos, 4210752, false);
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.gui(key));
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.gui(key), values);
    }
}
