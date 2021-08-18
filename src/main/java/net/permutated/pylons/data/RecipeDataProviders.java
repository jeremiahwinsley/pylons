package net.permutated.pylons.data;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

/**
 * @author Commoble
 * @see <a href="https://gist.github.com/Commoble/c491e53fce6a3902f22399735e11412d#file-recipedataproviders-java">source gist</a>
 */
public class RecipeDataProviders {
    /**
     * Make a dataprovider for a shaped recipe that doesn't require an itemgroup and has no advancement
     *
     * @param generator          generator from the gather data event
     * @param recipeID           ID of the recipe json to use (should be the ID of the result item for most cases)
     * @param resultItemProvider the result item of the recipe
     * @param resultItemCount    the size of the result itemstack
     * @param pattern            The shaped recipe crafting pattern
     * @param key                The key to the shaped recipe crafting pattern
     * @return a data provider that can be run to generate data
     */
    public static IDataProvider makeShapedRecipeProvider(DataGenerator generator, ResourceLocation recipeID, IItemProvider resultItemProvider, int resultItemCount, List<String> pattern, Map<Character, Ingredient> key) {
        return new RecipeProvider(generator) {
            @Override
            protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
                Item resultItem = resultItemProvider.asItem();
                ShapedRecipeBuilder builder = new ShapedRecipeBuilder(resultItem, resultItemCount);
                consumer.accept(builder.new Result(
                                    recipeID,
                                    resultItem,
                                    resultItemCount,
                                    "", // recipe book group (not used)
                                    pattern,
                                    key,
                                    null, // advancement (not used)
                                    null) // advancement ID
                                {
                                    @Override
                                    public JsonObject serializeAdvancement() {
                                        return null;
                                    }
                                }
                );

            }
        };
    }

    /**
     * Make a dataprovider for a shapeless recipe that doesn't require an itemgroup and has no advancement
     *
     * @param generator          generator from the gather data event
     * @param recipeID           ID of the recipe json to use (should be the ID of the result item for most cases)
     * @param resultItemProvider the result item of the recipe
     * @param resultItemCount    the size of the result itemstack
     * @param ingredients        The ingredient list for the recipe
     * @return a data provider that can be run to generate data
     */
    public static IDataProvider makeShapelessRecipeProvider(DataGenerator generator, ResourceLocation recipeID, IItemProvider resultItemProvider, int resultItemCount, List<Ingredient> ingredients) {
        return new RecipeProvider(generator) {
            @Override
            protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
                Item resultItem = resultItemProvider.asItem();
                consumer.accept(new ShapelessRecipeBuilder.Result(
                                    recipeID,
                                    resultItem,
                                    resultItemCount,
                                    "", // recipe book group (not used)
                                    ingredients,
                                    null, // advancement (not used)
                                    null) // advancement ID
                                {
                                    @Override
                                    public JsonObject serializeAdvancement() {
                                        return null;
                                    }
                                }
                );

            }
        };
    }

    /**
     * Make a data provider for a regular furnace recipe without advancements or recipe book stuff, using standard 200-tick cooking time
     *
     * @param generator          The data generator from the gather data event
     * @param recipeID           The ID for the recipe json to be generated
     * @param ingredient         The ingredient of the recipe
     * @param resultItemProvider The result of the recipe
     * @param xp                 The xp to be granted by the recipe
     * @return A data provider that can be run to generate a recipe json
     */
    public static IDataProvider makeFurnaceRecipeProvider(DataGenerator generator, ResourceLocation recipeID, Ingredient ingredient, IItemProvider resultItemProvider, float xp) {
        return makeCookingRecipeProvider(generator, recipeID, ingredient, resultItemProvider, xp, 200, IRecipeSerializer.SMELTING_RECIPE);
    }


    /**
     * Make a data provider for a blast furnace recipe without advancements or recipe book stuff, using standard 100-tick cooking time
     *
     * @param generator          The data generator from the gather data event
     * @param recipeID           The ID for the recipe json to be generated
     * @param ingredient         The ingredient of the recipe
     * @param resultItemProvider The result of the recipe
     * @param xp                 The xp to be granted by the recipe
     * @return A data provider that can be run to generate a recipe json
     */
    public static IDataProvider makeBlastingRecipeProvider(DataGenerator generator, ResourceLocation recipeID, Ingredient ingredient, IItemProvider resultItemProvider, float xp) {
        return makeCookingRecipeProvider(generator, recipeID, ingredient, resultItemProvider, xp, 100, IRecipeSerializer.BLASTING_RECIPE);
    }

    /**
     * Make a data provider for a cooking recipe without advancements or recipe book stuff
     *
     * @param generator          The data generator from the gather data event
     * @param recipeID           The ID of the recipe json to generate
     * @param ingredient         The ingredient of the cooking recipe
     * @param resultItemProvider The item result of the recipe
     * @param xp                 The xp to be granted by the recipe
     * @param time               The time in ticks needed for the furnace to spend cooking (typically 200 for furnaces or 100 for blast furnaces)
     * @param recipeType         The IRecipeSerializer to use for the cooking recipe (furnace, blasting, smoking, etc)
     * @return A data provider that can be run to generate a recipe json
     */
    public static IDataProvider makeCookingRecipeProvider(DataGenerator generator, ResourceLocation recipeID, Ingredient ingredient, IItemProvider resultItemProvider, float xp, int time, IRecipeSerializer<? extends AbstractCookingRecipe> recipeType) {
        return new RecipeProvider(generator) {
            @Override
            protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
                Item item = resultItemProvider.asItem();
                consumer.accept(
                    new CookingRecipeBuilder.Result(recipeID, "", ingredient, item, xp, time, null, null, recipeType) {
                        @Override
                        public JsonObject serializeAdvancement() {
                            return null;
                        }
                    });
            }

        };
    }

    public static IDataProvider makeStonecuttingRecipeProvider(DataGenerator generator, ResourceLocation recipeID, Ingredient ingredient, IItemProvider resultItemProvider) {
        return new RecipeProvider(generator) {
            @Override
            protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
                consumer.accept(
                    new SingleItemRecipeBuilder.Result(recipeID, IRecipeSerializer.STONECUTTER, "", ingredient, resultItemProvider.asItem(), 1, null, null) {
                        @Override
                        public JsonObject serializeAdvancement() {
                            return null;
                        }
                    });
            }

        };
    }
}
