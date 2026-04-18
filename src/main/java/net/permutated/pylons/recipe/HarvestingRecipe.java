package net.permutated.pylons.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.util.Constants;

import java.util.Optional;

public class HarvestingRecipe implements Recipe<HarvestingRecipeInput> {
    private static final Codec<Block> BLOCK_CODEC = BuiltInRegistries.BLOCK.byNameCodec();
    private static final MapCodec<HarvestingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BLOCK_CODEC.fieldOf(Constants.JSON.BLOCK).forGetter(HarvestingRecipe::getInput),
        ItemStackTemplate.CODEC.fieldOf(Constants.JSON.OUTPUT).forGetter(HarvestingRecipe::getOutput)
    ).apply(instance, HarvestingRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Block> BLOCK_STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK);
    private static final StreamCodec<RegistryFriendlyByteBuf, HarvestingRecipe> STREAM_CODEC = StreamCodec.composite(
        BLOCK_STREAM_CODEC, HarvestingRecipe::getInput,
        ItemStackTemplate.STREAM_CODEC, HarvestingRecipe::getOutput,
        HarvestingRecipe::new
    );

    public static final RecipeSerializer<HarvestingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private final Block input;
    private final ItemStackTemplate output;
    private final IntegerProperty ageProperty;

    public HarvestingRecipe(Block input, ItemStackTemplate output) {
        Preconditions.checkNotNull(input, "block cannot be null.");
        Preconditions.checkNotNull(output, "output cannot be null.");

        Optional<IntegerProperty> property = input.defaultBlockState().getProperties().stream()
            .filter(key -> key instanceof IntegerProperty ip && ip.getName().equals("age"))
            .map(IntegerProperty.class::cast)
            .findFirst();

        Preconditions.checkState(property.isPresent(), "block must have an age property.");
        Preconditions.checkState(output.count() > 0, "output cannot be empty.");

        this.input = input;
        this.output = output;
        this.ageProperty = property.get();
    }

    @Override
    public RecipeType<HarvestingRecipe> getType() {
        return ModRegistry.HARVESTING_RECIPE_TYPE.get();
    }

    @Override
    public RecipeSerializer<HarvestingRecipe> getSerializer() {
        return SERIALIZER;
    }

    public Block getInput() {
        return input;
    }

    public ItemStackTemplate getOutput() {
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

    @Override
    public boolean matches(HarvestingRecipeInput input, Level level) {
        return this.input.equals(input.block());
    }

    @Override
    public ItemStack assemble(HarvestingRecipeInput harvestingRecipeInput) {
        return this.output.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRegistry.HARVESTING_RECIPE_CATEGORY.get();
    }
}
