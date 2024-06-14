package net.permutated.pylons.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.permutated.pylons.util.Constants;

import java.util.UUID;

public record PlayerComponent(UUID uuid, String name) {
    public static Codec<PlayerComponent> BASIC_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            UUIDUtil.CODEC.fieldOf(Constants.NBT.UUID).forGetter(PlayerComponent::uuid),
            Codec.STRING.fieldOf(Constants.NBT.NAME).forGetter(PlayerComponent::name)
        ).apply(instance, PlayerComponent::new)
    );
}
