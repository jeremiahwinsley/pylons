package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.BlockComponent;
import net.permutated.pylons.util.TranslationKey;

import java.util.function.Consumer;

public class BlockFilterCard extends Item {

    public BlockFilterCard(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getItemInHand().getItem() instanceof BlockFilterCard && context.getLevel() instanceof ServerLevel level) {
            BlockPos clickedPos = context.getClickedPos();
            BlockState blockState = level.getBlockState(clickedPos);
            if (!blockState.isAir()) {
                Block block = blockState.getBlock();
                Identifier key = BuiltInRegistries.BLOCK.getKey(block);
                String name = block.getDescriptionId();

                context.getItemInHand().set(ModRegistry.BLOCK_COMPONENT, new BlockComponent(key, name));
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getComponents().has(ModRegistry.BLOCK_COMPONENT.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, builder, flagIn);

        BlockComponent component = stack.get(ModRegistry.BLOCK_COMPONENT);
        if (component != null) {
            String name = component.descriptionId();
            builder.accept(Component.translatable(name).withStyle(ChatFormatting.BLUE));

            builder.accept(Component.empty());
            builder.accept(translate("insert1"));
            builder.accept(translate("insert2"));
        } else {
            builder.accept(translate("no_block"));
        }

        builder.accept(Component.empty());
        builder.accept(translate("protection"));
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }
}
