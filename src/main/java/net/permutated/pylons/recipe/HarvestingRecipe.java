package net.permutated.pylons.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.util.Constants;

import java.util.Optional;

public class HarvestingRecipe implements Recipe<HarvestingRecipeInput> {
    private final Block input;
    private final ItemStack output;
    private final IntegerProperty ageProperty;

    public HarvestingRecipe(Block input, ItemStack output) {
        Preconditions.checkNotNull(input, "block cannot be null.");
        Preconditions.checkNotNull(output, "output cannot be null.");

        Optional<IntegerProperty> property = input.defaultBlockState().getValues().keySet().stream()
            .filter(key -> key instanceof IntegerProperty ip && ip.getName().equals("age"))
            .map(IntegerProperty.class::cast)
            .findFirst();

        Preconditions.checkState(property.isPresent(), "block must have an age property.");
        Preconditions.checkState(!output.isEmpty(), "output cannot be empty.");

        this.input = input;
        this.output = output;
        this.ageProperty = property.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRegistry.HARVESTING_RECIPE_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.HARVESTING_RECIPE_SERIALIZER.get();
    }

    public Block getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public IntegerProperty getAgeProperty() {
        return ageProperty;
    }

    public int getMaxAge() {
        return ageProperty.max;
    }

    public int getMinAge() {
        return ageProperty.min;
    }

    public static class Serializer implements RecipeSerializer<HarvestingRecipe> {
        private static final Codec<Block> BLOCK_CODEC = BuiltInRegistries.BLOCK.byNameCodec();
        private static final MapCodec<HarvestingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BLOCK_CODEC.fieldOf(Constants.JSON.BLOCK).forGetter(HarvestingRecipe::getInput),
            ItemStack.CODEC.fieldOf(Constants.JSON.OUTPUT).forGetter(HarvestingRecipe::getOutput)
        ).apply(instance, HarvestingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, Block> BLOCK_STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK);
        private static final StreamCodec<RegistryFriendlyByteBuf, HarvestingRecipe> STREAM_CODEC = StreamCodec.composite(
            BLOCK_STREAM_CODEC, HarvestingRecipe::getInput,
            ItemStack.STREAM_CODEC, HarvestingRecipe::getOutput,
            HarvestingRecipe::new
        );

        @Override
        public MapCodec<HarvestingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HarvestingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    @Override
    public boolean matches(HarvestingRecipeInput input, Level level) {
        return this.input.equals(input.block());
    }

    @Override
    public ItemStack assemble(HarvestingRecipeInput input, HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output.copy();
    }
}
