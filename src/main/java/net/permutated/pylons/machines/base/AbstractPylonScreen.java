package net.permutated.pylons.machines.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.ResourceUtil;
import net.permutated.pylons.util.TextureHolder;
import net.permutated.pylons.util.TranslationKey;

import java.util.List;

public abstract class AbstractPylonScreen<T extends AbstractPylonContainer> extends AbstractContainerScreen<T> {
    protected final Identifier gui;
    protected final boolean usesEnergy;
    protected Button workButton;
    protected Button rangeButton;

    protected AbstractPylonScreen(T container, Inventory inventory, Component title) {
        this(container, inventory, title, false);
    }

    protected AbstractPylonScreen(T container, Inventory inv, Component name, boolean usesEnergy) {
        super(container, inv, name, 176, 172);
        this.usesEnergy = usesEnergy;
        this.gui = usesEnergy ? ResourceUtil.gui("pylon_energy") : ResourceUtil.gui("pylon");
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
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, gui, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        if (usesEnergy) {
            float energyFraction = this.menu.dataHolder.getEnergyFraction();
            var energyHolder = new TextureHolder(8, 54, 0, 172, 160, 16);
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                gui,
                relX + energyHolder.progressOffsetX(),
                relY + energyHolder.progressOffsetY(),
                energyHolder.textureOffsetX(),
                energyHolder.textureOffsetY(),
                energyHolder.getWidthFraction(energyFraction),
                energyHolder.textureHeight(),
                256,
                256
                );
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        String owner = this.menu.getOwnerName();

        Component component;
        if (owner.equals(Constants.UNKNOWN)) {
            component = translate("noOwner").withStyle(ChatFormatting.RED);
        } else {
            component = translate("owner", owner);
        }
        drawText(graphics, component, 30);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        if (usesEnergy && this.isHovering(8, 54, 160, 16, x, y)) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, List.of(
                translate("fluxBar"),
                translate("fluxData", this.menu.dataHolder.getEnergy(), this.menu.dataHolder.getMaxEnergy())
            ), x, y);
        }
    }

    protected void drawText(GuiGraphicsExtractor graphics, Component component, int yPos) {
        graphics.text(this.font, component, 8, yPos, -12566464, false);
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.gui(key));
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.gui(key), values);
    }
}
