package net.permutated.pylons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
    public static final int MINIMUM = ConfigManager.COMMON.infusionMinimumDuration.get() * 20;

    // required duration to activate the filter in a pylon
    public static final int REQUIRED = ConfigManager.COMMON.infusionRequiredDuration.get() * 20;

    // duration applied to player each work cycle
    public static final int APPLIED = ConfigManager.COMMON.infusionAppliedDuration.get() * 20;

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof PotionFilterCard) {
            if (!level.isClientSide) {

                MobEffect effect = PotionFilterCard.getEffect(stack);
                int amplifier = PotionFilterCard.getAmplifier(stack);
                int duration = PotionFilterCard.getDuration(stack);

                if (duration >= PotionFilterCard.REQUIRED) {
                    return InteractionResultHolder.success(stack);
                }

                Optional<MobEffectInstance> active;
                if (effect == null) {
                    active = player.getActiveEffects().stream()
                        // Require a configured minimum effect length
                        .filter(effectInstance -> effectInstance.getDuration() >= MINIMUM)
                        .findFirst();
                } else {
                    active = player.getActiveEffects().stream()
                        // Require a configured minimum effect length
                        .filter(effectInstance -> effectInstance.getDuration() >= MINIMUM)
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

            var registryName = effect.getRegistryName();
            if (registryName != null && !isWhitelisted(registryName)) {
                tooltip.add(translate("effect_blacklisted").withStyle(ChatFormatting.RED));
            }

            tooltip.add(new TextComponent(""));

            if (duration >= REQUIRED) {
                tooltip.add(translate("insert1"));
                tooltip.add(translate("insert2"));
                tooltip.add(translate("activated").withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(translate("increase1"));
                tooltip.add(translate("increase2"));
                tooltip.add(translate("progress", display, REQUIRED / 20).withStyle(ChatFormatting.RED));
            }
        } else {
            tooltip.add(translate("no_effect1"));
            tooltip.add(translate("no_effect2"));
            tooltip.add(translate("minimum_duration", MINIMUM / 20));
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
        ResourceLocation registryName = effect.getRegistryName();
        if (registryName == null) {
            return stack;
        }

        CompoundTag tag = new CompoundTag();
        tag.putString(Constants.NBT.EFFECT, registryName.toString());
        tag.putInt(Constants.NBT.AMPLIFIER, amplifier);
        tag.putInt(Constants.NBT.DURATION, Math.min(REQUIRED, duration));

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
        if (current >= REQUIRED) {
            return stack;
        }

        int total = Math.min(REQUIRED, current + duration);

        tag.putInt(Constants.NBT.DURATION, total);
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

    public static boolean isAllowedEffect(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(Pylons.MODID);
        if (tag != null && tag.contains(Constants.NBT.EFFECT)) {
            String effectName = tag.getString(Constants.NBT.EFFECT);
            return isWhitelisted(new ResourceLocation(effectName));
        }
        return false;
    }

    protected static boolean isWhitelisted(ResourceLocation location) {
        var whitelist = ConfigManager.COMMON.infusionEffectWhitelist.get();
        return whitelist.contains(location.getNamespace()) || whitelist.contains(location.toString());
    }

    protected MutableComponent translate(String key) {
        return new TranslatableComponent(TranslationKey.tooltip(key)).withStyle(ChatFormatting.GRAY);
    }

    protected TranslatableComponent translate(String key, Object... values) {
        return new TranslatableComponent(TranslationKey.tooltip(key), values);
    }

    protected TranslatableComponent withAmplifier(MutableComponent component, int amplifier) {
        return new TranslatableComponent("potion.withAmplifier", component,
            new TranslatableComponent("potion.potency." + amplifier));
    }
}
