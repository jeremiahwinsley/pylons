package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.PotionComponent;
import net.permutated.pylons.util.TranslationKey;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class PotionFilterCard extends Item {

    public PotionFilterCard(Properties properties) {
        super(properties.stacksTo(1));
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof PotionFilterCard) {
            if (!level.isClientSide()) {
                PotionComponent data = stack.get(ModRegistry.POTION_COMPONENT);

                if (data != null && data.duration() >= PotionFilterCard.getRequiredDuration()) {
                    return InteractionResult.SUCCESS;
                }

                // handle transferring effects between cards
                if (hand == InteractionHand.MAIN_HAND && data != null) { // this should only run for cards held in the main hand
                    final ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (offhand.getItem() instanceof PotionFilterCard) { // offhand is holding a potion filter card
                        PotionComponent offhandData = offhand.get(ModRegistry.POTION_COMPONENT);
                        if (offhandData != null && data.matches(offhandData)) {
                            // main hand card has the same effect and amplifier
                            // merge effect from off hand card with main hand card
                            player.setItemInHand(hand, addDuration(stack, offhandData.duration()));
                            // clear potion data from offhand card
                            player.setItemInHand(InteractionHand.OFF_HAND, clearEffect(offhand));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

                Optional<MobEffectInstance> active;
                if (data == null) {
                    active = player.getActiveEffects().stream()
                        // Require a configured minimum effect length
                        .filter(effectInstance -> effectInstance.getDuration() >= getMinimumDuration())
                        .findFirst();
                } else {
                    active = player.getActiveEffects().stream()
                        // Require a configured minimum effect length
                        .filter(effectInstance -> effectInstance.getDuration() >= getMinimumDuration())
                        // Effect must match saved effect
                        .filter(effectInstance -> effectInstance.getEffect().is(data.effect()))
                        // Amplifier must match saved amplifier
                        .filter(effectInstance -> Objects.equals(effectInstance.getAmplifier(), data.amplifier()))
                        .findFirst();
                }


                if (active.isPresent()) {
                    Holder<MobEffect> activeEffect = active.get().getEffect();
                    int activeAmplifier = active.get().getAmplifier();
                    int activeDuration = active.get().getDuration();

                    ItemStack copy;
                    if (data == null) {
                        // new effect
                        copy = withEffect(stack, activeEffect, activeAmplifier, activeDuration);
                    } else {
                        // same effect
                        copy = addDuration(stack, activeDuration);
                    }

                    player.setItemInHand(hand, copy);
                    player.removeEffect(activeEffect);
                    return InteractionResult.SUCCESS;
                }
            } else {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getComponents().has(ModRegistry.POTION_COMPONENT.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);

        PotionComponent data = stack.get(ModRegistry.POTION_COMPONENT);

        if (data != null) {
            MobEffect effect = data.effect().value();
            int duration = data.duration();
            int amplifier = data.amplifier();

            // if duration in ticks is less than 20, display 0.
            // Otherwise, divide by 20 to get duration in seconds.
            int seconds = (duration < 20) ? 0 : duration / 20;


            MutableComponent component = effect.getDisplayName().copy();
            if (amplifier > 0) {
                component = withAmplifier(component, amplifier);
            }

            if (effect.isBeneficial()) {
                builder.accept(component.withStyle(ChatFormatting.BLUE));
            } else {
                builder.accept(component.withStyle(ChatFormatting.RED));
            }

            if (isBanned(stack)) {
                builder.accept(translate("effect_banned").withStyle(ChatFormatting.RED));
            }

            if (!isAllowed(stack)) {
                builder.accept(translate("effect_denied").withStyle(ChatFormatting.RED));
            }

            if (limitedPotency(stack)) {
                String potencyKey = String.format("potion.potency.%d", ConfigManager.SERVER.infusionMaximumPotency.getAsInt());
                builder.accept(translate("potency_capped", Component.translatable(potencyKey)).withStyle(ChatFormatting.YELLOW));
            }

            builder.accept(Component.empty());

            if (duration >= getRequiredDuration()) {
                builder.accept(translate("insert1"));
                builder.accept(translate("insert2"));
                builder.accept(translate("activated").withStyle(ChatFormatting.GREEN));
            } else {
                builder.accept(translate("increase1"));
                builder.accept(translate("increase2"));
                builder.accept(translate("progress", seconds, getRequiredDuration() / 20).withStyle(ChatFormatting.RED));
            }
        } else {
            builder.accept(translate("no_effect1"));
            builder.accept(translate("no_effect2"));
            builder.accept(translate("minimum_duration", getMinimumDuration() / 20));
        }

        builder.accept(Component.empty());
        builder.accept(translate("infusion"));
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
    public static ItemStack withEffect(final ItemStack stack, Holder<MobEffect> effect, int amplifier, int duration) {
        ItemStack copy = stack.copy();
        copy.set(ModRegistry.POTION_COMPONENT, new PotionComponent(effect, amplifier, duration));
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
        PotionComponent data = copy.get(ModRegistry.POTION_COMPONENT);

        if (data != null && data.duration() < getRequiredDuration()) {
            int current = data.duration();
            int total = Math.min(getRequiredDuration(), current + duration);

            copy.set(ModRegistry.POTION_COMPONENT, new PotionComponent(data.effect(), data.amplifier(), total));
        }

        return copy;
    }

    /**
     * Remove Pylons NBT from a potion filter card.
     *
     * @param stack the ItemStack containing NBT
     * @return a copy of the ItemStack without the pylons tag
     */
    public static ItemStack clearEffect(final ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.remove(ModRegistry.POTION_COMPONENT);
        return copy;
    }

    public static boolean isBanned(DataComponentHolder stack) {
        return Optional.ofNullable(stack.get(ModRegistry.POTION_COMPONENT))
            .map(PotionComponent::effect)
            .map(holder -> holder.is(ModRegistry.INFUSION_BANNED))
            .orElse(false);
    }

    public static boolean isAllowed(DataComponentHolder stack) {
        return Optional.ofNullable(stack.get(ModRegistry.POTION_COMPONENT))
            .map(PotionComponent::effect)
            .flatMap(Holder::unwrapKey)
            .map(ResourceKey::identifier)
            .map(location -> isAllowedEffect(location) && !isDeniedEffect(location))
            .orElse(false);
    }

    public static boolean limitedPotency(DataComponentHolder stack) {
        return Optional.ofNullable(stack.get(ModRegistry.POTION_COMPONENT))
            .map(PotionComponent::amplifier)
            .map(amplifier -> amplifier > ConfigManager.SERVER.infusionMaximumPotency.getAsInt())
            .orElse(false);
    }

    protected static boolean isAllowedEffect(Identifier location) {
        var allowed = ConfigManager.SERVER.infusionAllowedEffects.get();
        return allowed.isEmpty() || allowed.contains(location.getNamespace()) || allowed.contains(location.toString());
    }

    protected static boolean isDeniedEffect(Identifier location) {
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
