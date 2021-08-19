package net.permutated.pylons.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

import java.util.function.Consumer;


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
            .pattern("qqq")
            .pattern("idi")
            .pattern("bbb")
            .define('d', Items.DIAMOND_BLOCK)
            .define('b', Items.POLISHED_BLACKSTONE)
            .define('q', Items.QUARTZ_SLAB)
            .define('i', Items.IRON_BARS)
            .unlockedBy("has_diamond_block", has(Items.DIAMOND_BLOCK))
            .save(consumer);

        shaped(ModRegistry.PLAYER_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.GEMS_DIAMOND)
            .define('g', Tags.Items.GLASS)
            .unlockedBy("has_expulsion_pylon", has(ModRegistry.EXPULSION_PYLON_ITEM.get()))
            .save(consumer);
    }

}
