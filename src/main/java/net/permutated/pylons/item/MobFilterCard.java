package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.EntityComponent;
import net.permutated.pylons.util.TranslationKey;

import java.util.function.Consumer;

public class MobFilterCard extends Item {

    public MobFilterCard(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void onPlayerInteractEvent(final PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof MobFilterCard && event.getTarget() instanceof LivingEntity) {
            if (event.getSide() == LogicalSide.SERVER) {
                Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(event.getTarget().getType());
                String name = event.getTarget().getType().getDescriptionId();

                itemStack.set(ModRegistry.ENTITY_COMPONENT, new EntityComponent(key, name));

                event.setCancellationResult(InteractionResult.SUCCESS);
            } else {
                event.setCancellationResult(InteractionResult.CONSUME);
            }
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getComponents().has(ModRegistry.ENTITY_COMPONENT.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);

        EntityComponent component = stack.get(ModRegistry.ENTITY_COMPONENT);
        if (component != null) {
            String name = component.descriptionId();
            builder.accept(Component.translatable(name).withStyle(ChatFormatting.BLUE));

            builder.accept(Component.empty());
            builder.accept(translate("insert1"));
            builder.accept(translate("insert2"));
        } else {
            builder.accept(translate("no_mob"));
        }

        builder.accept(Component.empty());
        builder.accept(translate("interdiction"));
        builder.accept(translate("protection"));
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }
}
