package net.permutated.pylons.data.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.data.RecipeException;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.SerializerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BeaconEffectRecipeBuilder extends AbstractRecipeBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final ItemStack output;

    @Override
    protected String getPrefix() {
        return Constants.BEACON_PYLON;
    }

    public BeaconEffectRecipeBuilder(ItemStack output) {
        this.output = output;
    }

    public static BeaconEffectRecipeBuilder builder(Item output, int count) {
        return new BeaconEffectRecipeBuilder(new ItemStack(output, count));
    }

    public static BeaconEffectRecipeBuilder builder(Item output) {
        return new BeaconEffectRecipeBuilder(new ItemStack(output));
    }

    public BeaconEffectRecipeBuilder addInput(Ingredient input) {
        if (ingredients.size() < 3) {
            ingredients.add(input);
        }
        return this;
    }

    public BeaconEffectRecipeBuilder addInput(IItemProvider input) {
        return addInput(Ingredient.of(input));
    }

    public BeaconEffectRecipeBuilder addInput(ITag<Item> input) {
        return addInput(Ingredient.of(input));
    }

    protected void validate(ResourceLocation id) {
        if (ingredients.isEmpty()) {
            throw new RecipeException(id.toString(), "recipe must have at least 1 ingredient");
        }

        if (ingredients.size() > 3) {
            throw new RecipeException(id.toString(), "recipe cannot have more than 3 ingredients");
        }

        for (Ingredient ingredient : ingredients) {
            if (Ingredient.EMPTY.equals(ingredient)) {
                throw new RecipeException(id.toString(), "ingredients cannot be empty");
            }
        }
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        String path = Objects.requireNonNull(output.getItem().getRegistryName()).getPath();
        build(consumer, id(path));
    }

    @Override
    protected AbstractResult getResult(ResourceLocation id) {
        return new Result(id);
    }

    public class Result extends AbstractResult {
        public Result(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            JsonArray input = new JsonArray();
            ingredients.forEach(ingredient -> input.add(ingredient.toJson()));

            jsonObject.add(Constants.JSON.INPUT, input);
            jsonObject.add(Constants.JSON.OUTPUT, SerializerUtil.serializeItemStack(output));
        }

        @Override
        public IRecipeSerializer<?> getType() {
            return ModRegistry.BEACON_EFFECT_SERIALIZER.get();
        }

    }
}
