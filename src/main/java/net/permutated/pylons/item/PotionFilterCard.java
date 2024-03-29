package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PotionFilterCard extends Item {
    public PotionFilterCard() {
        super(new Item.Properties().stacksTo(1).setNoRepair());
    }

    // minimum duration of effect that can be copied to the filter
    public static int getMinimumDuration() {
        return ConfigManager.SERVER.infusionMinimumDuration.get() * 20;
    }

    // required duration to activate the filter in a pylon
    public static int getRequiredDuration() {
        return ConfigManager.SERVER.infusionRequiredDuration.get() * 20;
    }

    // duration applied to player each work cycle
    public static int getAppliedDuration() {
        return ConfigManager.SERVER.infusionAppliedDuration.get() * 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof PotionFilterCard) {
            if (!level.isClientSide) {

                MobEffect effect = PotionFilterCard.getEffect(stack);
                int amplifier = PotionFilterCard.getAmplifier(stack);
                int duration = PotionFilterCard.getDuration(stack);

                if (duration >= PotionFilterCard.getRequiredDuration()) {
                    return InteractionResultHolder.success(stack);
                }

                // handle transferring effects between cards
                if (hand == InteractionHand.MAIN_HAND) { // this should only run for cards held in the main hand
                    final ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (offhand.getItem() instanceof PotionFilterCard) { // offhand is holding a potion filter card
                        MobEffect offhandEffect = PotionFilterCard.getEffect(offhand);
                        int offhandAmplifier = PotionFilterCard.getAmplifier(offhand);
                        int offhandDuration = PotionFilterCard.getDuration(offhand);
                        if (offhandEffect != null) { // offhand card has an effect
                            ItemStack copy;
                            if (effect == null) {
                                // main hand card is blank
                                // move effect from off hand card to main hand card
                                copy = withEffect(stack, offhandEffect, offhandAmplifier, offhandDuration);
                                // clear potion data from offhand card
                                player.setItemInHand(InteractionHand.OFF_HAND, clearEffect(offhand));
                                return InteractionResultHolder.success(copy);
                            } else if(Objects.equals(effect, offhandEffect) && Objects.equals(amplifier, offhandAmplifier)) {
                                // main hand card has the same effect and amplifier
                                // merge effect from off hand card with main hand card
                                copy = addDuration(stack, offhandDuration);
                                // clear potion data from offhand card
                                player.setItemInHand(InteractionHand.OFF_HAND, clearEffect(offhand));
                                return InteractionResultHolder.success(copy);
                            }
                        }
                    }
                }


                Optional<MobEffectInstance> active;
                if (effect == null) {
                    active = player.getActiveEffects().stream()
                        // Require a configured minimum effect length
                        .filter(effectInstance -> effectInstance.getDuration() >= getMinimumDuration())
                        .findFirst();
                } else {
                    active = player.getActiveEffects().stream()
                        // Require a configured minimum effect length
                        .filter(effectInstance -> effectInstance.getDuration() >= getMinimumDuration())
                        // Effect must match saved effect
                        .filter(effectInstance -> Objects.equals(effectInstance.getEffect(), effect))
                        // Amplifier must match saved amplifier
                        .filter(effectInstance -> Objects.equals(effectInstance.getAmplifier(), amplifier))
                        .findFirst();
                }


                if (active.isPresent()) {
                    MobEffect activeEffect = active.get().getEffect();
                    int activeAmplifier = active.get().getAmplifier();
                    int activeDuration = active.get().getDuration();

                    ItemStack copy;
                    if (effect == null) {
                        // new effect
                        copy = withEffect(stack, activeEffect, activeAmplifier, activeDuration);
                    } else {
                        // same effect
                        copy = addDuration(stack, activeDuration);
                    }

                    player.removeEffect(activeEffect);
                    return InteractionResultHolder.success(copy);
                }
            } else {
                return InteractionResultHolder.consume(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        return (tag != null && tag.contains(Constants.NBT.EFFECT));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        MobEffect effect = getEffect(stack);
        int duration = getDuration(stack);
        int amplifier = getAmplifier(stack);

        // if duration in ticks is less than 20, display 0.
        // Otherwise, divide by 20 to get duration in seconds.
        int display = (duration < 20) ? 0 : duration / 20;

        if (effect != null) {
            MutableComponent component = effect.getDisplayName().copy();
            if (amplifier > 0) {
                component = withAmplifier(component, amplifier);
            }

            if (effect.isBeneficial()) {
                tooltip.add(component.withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(component.withStyle(ChatFormatting.RED));
            }

            if (!isAllowed(stack)) {
                tooltip.add(translate("effect_denied").withStyle(ChatFormatting.RED));
            }

            tooltip.add(Component.empty());

            if (duration >= getRequiredDuration()) {
                tooltip.add(translate("insert1"));
                tooltip.add(translate("insert2"));
                tooltip.add(translate("activated").withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(translate("increase1"));
                tooltip.add(translate("increase2"));
                tooltip.add(translate("progress", display, getRequiredDuration() / 20).withStyle(ChatFormatting.RED));
            }
        } else {
            tooltip.add(translate("no_effect1"));
            tooltip.add(translate("no_effect2"));
            tooltip.add(translate("minimum_duration", getMinimumDuration() / 20));
        }
    }


    /**
     * Returns a copy of the ItemStack with a new effect tag added.
     * <p>
     * No validation on the effect is performed here.
     * This should be completed before calling this method.
     *
     * @param stack     the input ItemStack
     * @param effect    the Effect to be added
     * @param amplifier the effect amplifier
     * @param duration  the initial effect duration (in ticks)
     * @return a copy of the ItemStack with the new NBT
     */
    public static ItemStack withEffect(final ItemStack stack, MobEffect effect, int amplifier, int duration) {
        ResourceLocation registryName = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        if (registryName == null) {
            return stack;
        }

        CompoundTag tag = new CompoundTag();
        tag.putString(Constants.NBT.EFFECT, registryName.toString());
        tag.putInt(Constants.NBT.AMPLIFIER, amplifier);
        tag.putInt(Constants.NBT.DURATION, Math.min(getRequiredDuration(), duration));

        ItemStack copy = stack.copy();
        copy.addTagElement(Pylons.MODID, tag);
        return copy;
    }


    /**
     * Returns a copy of the ItemStack with the duration added to the existing amount.
     * <p>
     * No validation on the effect is performed here.
     * This should be completed before calling this method.
     *
     * @param stack    the input ItemStack
     * @param duration the duration (in ticks) to be added to the existing amount
     * @return a copy of the ItemStack with the new NBT
     */
    public static ItemStack addDuration(final ItemStack stack, int duration) {
        ItemStack copy = stack.copy();
        CompoundTag tag = copy.getOrCreateTagElement(Pylons.MODID);
        int current = tag.getInt(Constants.NBT.DURATION);

        // already activated
        if (current >= getRequiredDuration()) {
            return stack;
        }

        int total = Math.min(getRequiredDuration(), current + duration);

        tag.putInt(Constants.NBT.DURATION, total);
        return copy;
    }

    /**
     * Remove Pylons NBT from a potion filter card.
     * @param stack the ItemStack containing NBT
     * @return a copy of the ItemStack without the pylons tag
     */
    public static ItemStack clearEffect(final ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.removeTagKey(Pylons.MODID);
        return copy;
    }

    @Nullable
    public static MobEffect getEffect(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.EFFECT)) {
            String effectName = tag.getString(Constants.NBT.EFFECT);
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectName));
        }
        return null;
    }

    public static int getDuration(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.DURATION)) {
            return tag.getInt(Constants.NBT.DURATION);
        }
        return 0;
    }

    public static int getAmplifier(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.AMPLIFIER)) {
            return tag.getInt(Constants.NBT.AMPLIFIER);
        }
        return 0;
    }

    public static boolean isAllowed(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.EFFECT)) {
            String effectName = tag.getString(Constants.NBT.EFFECT);
            var location = new ResourceLocation(effectName);
            return isAllowedEffect(location) && !isDeniedEffect(location);
        }
        return false;
    }

    protected static boolean isAllowedEffect(ResourceLocation location) {
        var allowed = ConfigManager.SERVER.infusionAllowedEffects.get();
        return allowed.isEmpty() || allowed.contains(location.getNamespace()) || allowed.contains(location.toString());
    }

    protected static boolean isDeniedEffect(ResourceLocation location) {
        var denied = ConfigManager.SERVER.infusionDeniedEffects.get();
        return denied.contains(location.getNamespace()) || denied.contains(location.toString());
    }

    protected MutableComponent translate(String key) {
        return Component.translatable(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected MutableComponent translate(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }

    protected MutableComponent withAmplifier(MutableComponent component, int amplifier) {
        return Component.translatable("potion.withAmplifier", component,
            Component.translatable("potion.potency." + amplifier));
    }
}
