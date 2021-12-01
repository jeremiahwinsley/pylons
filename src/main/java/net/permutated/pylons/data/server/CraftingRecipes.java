package net.permutated.pylons.data.server;

import net.minecraft.data.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

import java.util.function.Consumer;

import static net.permutated.pylons.util.ResourceUtil.prefix;


import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

public class CraftingRecipes extends RecipeProvider {
    public CraftingRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    private ShapedRecipeBuilder shaped(ItemLike provider) {
        return ShapedRecipeBuilder.shaped(provider)
            .group(Pylons.MODID);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        // Expulsion Pylong
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

        ShapelessRecipeBuilder.shapeless(ModRegistry.PLAYER_FILTER.get())
            .group(Pylons.MODID)
            .requires(ModRegistry.PLAYER_FILTER.get())
            .unlockedBy("has_player_filter", has(ModRegistry.PLAYER_FILTER.get()))
            .save(consumer, prefix("clear_player_filter"));

        // Infusion Pylon
        shaped(ModRegistry.INFUSION_PYLON.get())
            .pattern("qqq")
            .pattern("idi")
            .pattern("bbb")
            .define('d', Items.EMERALD_BLOCK)
            .define('b', Items.POLISHED_BLACKSTONE)
            .define('q', Items.QUARTZ_SLAB)
            .define('i', Items.IRON_BARS)
            .unlockedBy("has_emerald_block", has(Items.EMERALD_BLOCK))
            .save(consumer);

        shaped(ModRegistry.POTION_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.GEMS_EMERALD)
            .define('g', Tags.Items.GLASS)
            .unlockedBy("has_infusion_pylon", has(ModRegistry.INFUSION_PYLON_ITEM.get()))
            .save(consumer);

        ShapelessRecipeBuilder.shapeless(ModRegistry.POTION_FILTER.get())
            .group(Pylons.MODID)
            .requires(ModRegistry.POTION_FILTER.get())
            .unlockedBy("has_potion_filter", has(ModRegistry.POTION_FILTER.get()))
            .save(consumer, prefix("clear_potion_filter"));

    }

}
