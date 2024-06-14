package net.permutated.pylons.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.permutated.pylons.util.Constants;

import java.util.Objects;

public record PotionComponent(Holder<MobEffect> effect, int amplifier, int duration) {
    public static Codec<PotionComponent> BASIC_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            MobEffect.CODEC.fieldOf(Constants.NBT.EFFECT).forGetter(PotionComponent::effect),
            Codec.INT.fieldOf(Constants.NBT.AMPLIFIER).forGetter(PotionComponent::amplifier),
            Codec.INT.fieldOf(Constants.NBT.DURATION).forGetter(PotionComponent::duration)
        ).apply(instance, PotionComponent::new)
    );

    public boolean matches(PotionComponent that) {
        return Objects.equals(this.effect, that.effect) && Objects.equals(this.amplifier, that.amplifier);
    }
}
