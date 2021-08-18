package net.permutated.pylons.data.server;

import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.registry.ItemTagRegistry;

import java.util.function.Consumer;

import static net.permutated.pylons.util.ResourceUtil.fromAE2;


public class CraftingRecipes extends RecipeProvider {
    public CraftingRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    private ShapedRecipeBuilder shaped(IItemProvider provider) {
        return ShapedRecipeBuilder.shaped(provider)
            .group(Pylons.MODID);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {

        shaped(ModRegistry.EXPULSION_PYLON.get())
            .pattern("lrl")
            .pattern("rrr")
            .pattern("lrl")
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('l', Tags.Items.INGOTS_IRON)
            .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
            .save(consumer);
    }

}
