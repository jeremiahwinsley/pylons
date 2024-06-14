package net.permutated.pylons.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.permutated.pylons.util.Constants;

public record EntityComponent(ResourceLocation registryKey, String descriptionId) {
    public static Codec<EntityComponent> BASIC_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf(Constants.NBT.REGISTRY).forGetter(EntityComponent::registryKey),
            Codec.STRING.fieldOf(Constants.NBT.NAME).forGetter(EntityComponent::descriptionId)
        ).apply(instance, EntityComponent::new)
    );
}
