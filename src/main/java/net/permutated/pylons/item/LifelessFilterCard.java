package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.permutated.pylons.util.TranslationKey;

import java.util.function.Consumer;

public class LifelessFilterCard extends Item {
    public LifelessFilterCard(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);

        builder.accept(translate("lifeless1").withStyle(ChatFormatting.DARK_GREEN));
        builder.accept(translate("lifeless2").withStyle(ChatFormatting.DARK_GREEN));
        builder.accept(translate("lifeless3").withStyle(ChatFormatting.DARK_RED));

        builder.accept(Component.empty());
        builder.accept(translate("interdiction").withStyle(ChatFormatting.GRAY));
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key));
    }
}
