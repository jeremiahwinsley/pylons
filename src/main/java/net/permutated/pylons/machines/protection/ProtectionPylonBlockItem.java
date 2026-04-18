package net.permutated.pylons.machines.protection;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

import static net.permutated.pylons.util.TranslationKey.translateTooltip;

public class ProtectionPylonBlockItem extends BlockItem {

    public ProtectionPylonBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @SuppressWarnings("deprecation") // deprecated by Mojang
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        builder.accept(translateTooltip("protection1"));
        builder.accept(translateTooltip("protection2"));
        builder.accept(translateTooltip("protection3"));
    }
}
