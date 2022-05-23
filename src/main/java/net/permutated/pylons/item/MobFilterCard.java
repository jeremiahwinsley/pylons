package net.permutated.pylons.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class MobFilterCard extends Item {
    public MobFilterCard() {
        super(new Properties().stacksTo(1).tab(ModRegistry.CREATIVE_TAB).setNoRepair());
    }

    public static void onPlayerInteractEvent(final PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof MobFilterCard && event.getTarget() instanceof LivingEntity) {
            if (event.getSide() == LogicalSide.SERVER) {
                CompoundNBT tag = itemStack.getOrCreateTagElement(Pylons.MODID);
                tag.putString(Constants.NBT.REGISTRY, Objects.toString(event.getTarget().getType().getRegistryName(), "unregistered"));
                tag.putString(Constants.NBT.NAME, event.getTarget().getType().getDescriptionId());

                event.setCancellationResult(ActionResultType.SUCCESS);
            } else {
                event.setCancellationResult(ActionResultType.CONSUME);
            }
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        return (tag != null && tag.contains(Constants.NBT.REGISTRY));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        if (tag != null) {
            String name = tag.getString(Constants.NBT.NAME);
            tooltip.add(new TranslationTextComponent(name).withStyle(TextFormatting.BLUE));

            tooltip.add(new StringTextComponent(""));
            tooltip.add(translate("insert1"));
            tooltip.add(translate("insert2"));
        } else {
            tooltip.add(translate("no_mob"));
        }
    }

    protected IFormattableTextComponent translate(String key) {
        return new TranslationTextComponent(TranslationKey.tooltip(key)).withStyle(TextFormatting.GRAY);
    }

    protected TranslationTextComponent translate(String key, Object... values) {
        return new TranslationTextComponent(TranslationKey.tooltip(key), values);
    }
}
