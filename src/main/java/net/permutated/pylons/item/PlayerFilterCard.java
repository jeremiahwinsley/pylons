package net.permutated.pylons.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
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
        if (itemStack.getItem() instanceof PlayerFilterCard && event.getTarget() instanceof PlayerEntity) {
            if (event.getSide() == LogicalSide.SERVER) {
                CompoundNBT tag = itemStack.getOrCreateTagElement(Pylons.MODID);
                tag.putUUID(Constants.NBT.UUID, event.getTarget().getUUID());
                tag.putString(Constants.NBT.NAME, getProfileName(event.getTarget()));

                event.setCancellationResult(ActionResultType.SUCCESS);
            } else {
                event.setCancellationResult(ActionResultType.CONSUME);
            }
            event.setCanceled(true);
        }
    }

    protected static String getProfileName(Entity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            return player.getGameProfile().getName();
        }
        return "unknown";
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        return (tag != null && tag.hasUUID(Constants.NBT.UUID));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        if (tag != null) {
            String username = tag.getString(Constants.NBT.NAME);
            tooltip.add(translate("player", username).withStyle(TextFormatting.BLUE));

            tooltip.add(new StringTextComponent(""));
            tooltip.add(translate("insert1"));
            tooltip.add(translate("insert2"));
        } else {
            tooltip.add(translate("no_player"));
        }
    }

    protected IFormattableTextComponent translate(String key) {
        return new TranslationTextComponent(TranslationKey.tooltip(key)).withStyle(TextFormatting.GRAY);
    }

    protected TranslationTextComponent translate(String key, Object... values) {
        return new TranslationTextComponent(TranslationKey.tooltip(key), values);
    }
}
