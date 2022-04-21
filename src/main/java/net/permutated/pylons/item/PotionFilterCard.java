package net.permutated.pylons.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PotionFilterCard extends Item {
    public PotionFilterCard() {
        super(new Item.Properties().stacksTo(1).tab(ModRegistry.CREATIVE_TAB).setNoRepair());
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
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof PotionFilterCard) {
            if (!level.isClientSide) {

                Effect effect = PotionFilterCard.getEffect(stack);
                int amplifier = PotionFilterCard.getAmplifier(stack);
                int duration = PotionFilterCard.getDuration(stack);

                if (duration >= getRequiredDuration()) {
                    return ActionResult.success(stack);
                }

                // handle transferring effects between cards
                if (hand == Hand.MAIN_HAND) { // this should only run for cards held in the main hand
                    final ItemStack offhand = player.getItemInHand(Hand.OFF_HAND);
                    if (offhand.getItem() instanceof PotionFilterCard) { // offhand is holding a potion filter card
                        Effect offhandEffect = PotionFilterCard.getEffect(offhand);
                        int offhandAmplifier = PotionFilterCard.getAmplifier(offhand);
                        int offhandDuration = PotionFilterCard.getDuration(offhand);
                        if (offhandEffect != null) { // offhand card has an effect
                            ItemStack copy;
                            if (effect == null) {
                                // main hand card is blank
                                // move effect from off hand card to main hand card
                                copy = withEffect(stack, offhandEffect, offhandAmplifier, offhandDuration);
                                // clear potion data from offhand card
                                player.setItemInHand(Hand.OFF_HAND, clearEffect(offhand));
                                return ActionResult.success(copy);
                            } else if(Objects.equals(effect, offhandEffect) && Objects.equals(amplifier, offhandAmplifier)) {
                                // main hand card has the same effect and amplifier
                                // merge effect from off hand card with main hand card
                                copy = addDuration(stack, offhandDuration);
                                // clear potion data from offhand card
                                player.setItemInHand(Hand.OFF_HAND, clearEffect(offhand));
                                return ActionResult.success(copy);
                            }
                        }
                    }
                }

                Optional<EffectInstance> active;
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
                    Effect activeEffect = active.get().getEffect();
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
                    return ActionResult.success(copy);
                }
            } else {
                return ActionResult.consume(stack);
            }
        }
        return ActionResult.pass(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        return (tag != null && tag.contains(Constants.NBT.EFFECT));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        Effect effect = getEffect(stack);
        int duration = getDuration(stack);
        int amplifier = getAmplifier(stack);

        // if duration in ticks is less than 20, display 0.
        // Otherwise, divide by 20 to get duration in seconds.
        int display = (duration < 20) ? 0 : duration / 20;

        if (effect != null) {
            IFormattableTextComponent component = effect.getDisplayName().copy();
            if (amplifier > 0) {
                component = withAmplifier(component, amplifier);
            }

            if (effect.isBeneficial()) {
                tooltip.add(component.withStyle(TextFormatting.BLUE));
            } else {
                tooltip.add(component.withStyle(TextFormatting.RED));
            }

            if (!isAllowed(stack)) {
                tooltip.add(translate("effect_denied").withStyle(TextFormatting.RED));
            }

            tooltip.add(new StringTextComponent(""));

            if (duration >= getRequiredDuration()) {
                tooltip.add(translate("insert1"));
                tooltip.add(translate("insert2"));
                tooltip.add(translate("activated").withStyle(TextFormatting.GREEN));
            } else {
                tooltip.add(translate("increase1"));
                tooltip.add(translate("increase2"));
                tooltip.add(translate("progress", display, getRequiredDuration() / 20).withStyle(TextFormatting.RED));
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
    public static ItemStack withEffect(final ItemStack stack, Effect effect, int amplifier, int duration) {
        ResourceLocation registryName = effect.getRegistryName();
        if (registryName == null) {
            return stack;
        }

        CompoundNBT tag = new CompoundNBT();
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
        CompoundNBT tag = copy.getOrCreateTagElement(Pylons.MODID);
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
    public static Effect getEffect(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.EFFECT)) {
            String effectName = tag.getString(Constants.NBT.EFFECT);
            return ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectName));
        }
        return null;
    }

    public static int getDuration(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.DURATION)) {
            return tag.getInt(Constants.NBT.DURATION);
        }
        return 0;
    }

    public static int getAmplifier(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.AMPLIFIER)) {
            return tag.getInt(Constants.NBT.AMPLIFIER);
        }
        return 0;
    }

    public static boolean isAllowed(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.EFFECT)) {
            String effectName = tag.getString(Constants.NBT.EFFECT);
            ResourceLocation location = new ResourceLocation(effectName);
            return isAllowedEffect(location) && !isDeniedEffect(location);
        }
        return false;
    }

    protected static boolean isAllowedEffect(ResourceLocation location) {
        List<? extends String> allowed = ConfigManager.SERVER.infusionAllowedEffects.get();
        return allowed.isEmpty() || allowed.contains(location.getNamespace()) || allowed.contains(location.toString());
    }

    protected static boolean isDeniedEffect(ResourceLocation location) {
        List<? extends String> denied = ConfigManager.SERVER.infusionDeniedEffects.get();
        return denied.contains(location.getNamespace()) || denied.contains(location.toString());
    }

    protected IFormattableTextComponent translate(String key) {
        return new TranslationTextComponent(TranslationKey.tooltip(key)).withStyle(TextFormatting.GRAY);
    }

    protected TranslationTextComponent translate(String key, Object... values) {
        return new TranslationTextComponent(TranslationKey.tooltip(key), values);
    }

    protected TranslationTextComponent withAmplifier(IFormattableTextComponent component, int amplifier) {
        return new TranslationTextComponent("potion.withAmplifier", component,
            new TranslationTextComponent("potion.potency." + amplifier));
    }
}
