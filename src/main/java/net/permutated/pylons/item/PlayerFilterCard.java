package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.PlayerComponent;
import net.permutated.pylons.util.TranslationKey;

import java.util.function.Consumer;

public class PlayerFilterCard extends Item {

    public PlayerFilterCard(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void onPlayerInteractEvent(final PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof PlayerFilterCard && event.getTarget() instanceof Player) {
            if (event.getSide() == LogicalSide.SERVER) {

                itemStack.set(ModRegistry.PLAYER_COMPONENT, new PlayerComponent(
                    event.getTarget().getUUID(),
                    getProfileName(event.getTarget())
                ));

                event.setCancellationResult(InteractionResult.SUCCESS);
            } else {
                event.setCancellationResult(InteractionResult.CONSUME);
            }
            event.setCanceled(true);
        }
    }

    protected static String getProfileName(Entity entity) {
        if (entity instanceof Player player) {
            return player.getGameProfile().name();
        }
        return "unknown";
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getComponents().has(ModRegistry.PLAYER_COMPONENT.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);

        PlayerComponent component = stack.get(ModRegistry.PLAYER_COMPONENT);
        if (component != null) {
            String username = component.name();
            builder.accept(translate("player", username).withStyle(ChatFormatting.BLUE));

            builder.accept(Component.empty());
            builder.accept(translate("insert1"));
            builder.accept(translate("insert2"));
        } else {
            builder.accept(translate("no_player"));
        }

        builder.accept(Component.empty());
        builder.accept(translate("expulsion"));
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }
}
