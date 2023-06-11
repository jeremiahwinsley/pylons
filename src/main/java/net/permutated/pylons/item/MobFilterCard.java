package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class MobFilterCard extends Item {
    public MobFilterCard() {
        super(new Properties().stacksTo(1).setNoRepair());
    }

    public static void onPlayerInteractEvent(final PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof MobFilterCard && event.getTarget() instanceof LivingEntity) {
            if (event.getSide() == LogicalSide.SERVER) {
                CompoundTag tag = itemStack.getOrCreateTagElement(Pylons.MODID);
                ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(event.getTarget().getType());
                tag.putString(Constants.NBT.REGISTRY, Objects.toString(key, "unregistered"));
                tag.putString(Constants.NBT.NAME, event.getTarget().getType().getDescriptionId());

                event.setCancellationResult(InteractionResult.SUCCESS);
            } else {
                event.setCancellationResult(InteractionResult.CONSUME);
            }
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        return (tag != null && tag.contains(Constants.NBT.REGISTRY));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);

        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        if (tag != null) {
            String name = tag.getString(Constants.NBT.NAME);
            tooltip.add(Component.translatable(name).withStyle(ChatFormatting.BLUE));

            tooltip.add(Component.empty());
            tooltip.add(translate("insert1"));
            tooltip.add(translate("insert2"));
        } else {
            tooltip.add(translate("no_mob"));
        }
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }
}
