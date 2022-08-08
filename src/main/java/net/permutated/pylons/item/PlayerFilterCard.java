package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;
import java.util.List;

public class PlayerFilterCard extends Item {
    public PlayerFilterCard() {
        super(new Properties().stacksTo(1).tab(ModRegistry.CREATIVE_TAB).setNoRepair());
    }

    public static void onPlayerInteractEvent(final PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof PlayerFilterCard && event.getTarget() instanceof Player) {
            if (event.getSide() == LogicalSide.SERVER) {
                CompoundTag tag = itemStack.getOrCreateTagElement(Pylons.MODID);
                tag.putUUID(Constants.NBT.UUID, event.getTarget().getUUID());
                tag.putString(Constants.NBT.NAME, getProfileName(event.getTarget()));

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
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        return (tag != null && tag.hasUUID(Constants.NBT.UUID));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        if (tag != null) {
            String username = tag.getString(Constants.NBT.NAME);
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
