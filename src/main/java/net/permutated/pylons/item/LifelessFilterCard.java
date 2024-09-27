package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.permutated.pylons.util.TranslationKey;

import java.util.List;

public class LifelessFilterCard extends Item {
    public LifelessFilterCard() {
        super(new Properties().stacksTo(1).setNoRepair());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(translate("lifeless1").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(translate("lifeless2").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(translate("lifeless3").withStyle(ChatFormatting.DARK_RED));

        tooltip.add(Component.empty());
        tooltip.add(translate("interdiction").withStyle(ChatFormatting.GRAY));
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key));
    }
}
