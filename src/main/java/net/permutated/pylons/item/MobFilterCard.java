package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.EntityComponent;
import net.permutated.pylons.util.TranslationKey;

import java.util.List;

public class MobFilterCard extends Item {

    public MobFilterCard() {
        super(new Properties().stacksTo(1).setNoRepair());
    }

    public static void onPlayerInteractEvent(final PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof MobFilterCard && event.getTarget() instanceof LivingEntity) {
            if (event.getSide() == LogicalSide.SERVER) {
                ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(event.getTarget().getType());
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        EntityComponent component = stack.get(ModRegistry.ENTITY_COMPONENT);
        if (component != null) {
            String name = component.descriptionId();
            tooltip.add(Component.translatable(name).withStyle(ChatFormatting.BLUE));

            tooltip.add(Component.empty());
            tooltip.add(translate("insert1"));
            tooltip.add(translate("insert2"));
        } else {
            tooltip.add(translate("no_mob"));
        }

        tooltip.add(Component.empty());
        tooltip.add(translate("interdiction"));
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }
}
