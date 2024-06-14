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
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.PlayerComponent;
import net.permutated.pylons.util.TranslationKey;

import java.util.List;

public class PlayerFilterCard extends Item {

    public PlayerFilterCard() {
        super(new Properties().stacksTo(1).setNoRepair());
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
            return player.getGameProfile().getName();
        }
        return "unknown";
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getComponents().has(ModRegistry.PLAYER_COMPONENT.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        PlayerComponent component = stack.get(ModRegistry.PLAYER_COMPONENT);
        if (component != null) {
            String username = component.name();
            tooltip.add(translate("player", username).withStyle(ChatFormatting.BLUE));

            tooltip.add(Component.empty());
            tooltip.add(translate("insert1"));
            tooltip.add(translate("insert2"));
        } else {
            tooltip.add(translate("no_player"));
        }
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }
}
