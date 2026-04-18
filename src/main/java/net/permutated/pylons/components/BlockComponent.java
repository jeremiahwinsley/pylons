package net.permutated.pylons.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.permutated.pylons.util.Constants;

public record BlockComponent(Identifier registryKey, String descriptionId) {
    public static Codec<BlockComponent> BASIC_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Identifier.CODEC.fieldOf(Constants.NBT.REGISTRY).forGetter(BlockComponent::registryKey),
            Codec.STRING.fieldOf(Constants.NBT.NAME).forGetter(BlockComponent::descriptionId)
        ).apply(instance, BlockComponent::new)
    );
}
