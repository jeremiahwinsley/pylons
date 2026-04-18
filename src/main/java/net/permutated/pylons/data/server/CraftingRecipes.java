package net.permutated.pylons.data.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.builder.HarvestingRecipeBuilder;

import java.util.concurrent.CompletableFuture;

import static net.permutated.pylons.util.ResourceUtil.recipe;

public class CraftingRecipes extends RecipeProvider {
    public CraftingRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }



    private ShapedRecipeBuilder shaped(ItemLike provider) {
        return super.shaped(RecipeCategory.MISC, provider)
            .group(Pylons.MODID);
    }

    @Override
    protected void buildRecipes() {
        // Expulsion Pylon
        shaped(ModRegistry.EXPULSION_PYLON.get())
            .pattern("qqq")
            .pattern("idi")
            .pattern("bbb")
            .define('d', Items.DIAMOND_BLOCK)
            .define('b', Items.POLISHED_BLACKSTONE)
            .define('q', Items.QUARTZ_SLAB)
            .define('i', Items.IRON_BARS)
            .unlockedBy("has_diamond_block", has(Items.DIAMOND_BLOCK))
            .save(output);

        shaped(ModRegistry.PLAYER_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.GEMS_DIAMOND)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_expulsion_pylon", has(ModRegistry.EXPULSION_PYLON_ITEM.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModRegistry.PLAYER_FILTER.get())
            .group(Pylons.MODID)
            .requires(ModRegistry.PLAYER_FILTER.get())
            .unlockedBy("has_player_filter", has(ModRegistry.PLAYER_FILTER.get()))
            .save(output, recipe("clear_player_filter"));

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
            .save(output);

        shaped(ModRegistry.POTION_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.GEMS_EMERALD)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_infusion_pylon", has(ModRegistry.INFUSION_PYLON_ITEM.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModRegistry.POTION_FILTER.get())
            .group(Pylons.MODID)
            .requires(ModRegistry.POTION_FILTER.get())
            .unlockedBy("has_potion_filter", has(ModRegistry.POTION_FILTER.get()))
            .save(output, recipe("clear_potion_filter"));

        // Harvester Pylon
        shaped(ModRegistry.HARVESTER_PYLON.get())
            .pattern("qqq")
            .pattern("idi")
            .pattern("bbb")
            .define('d', Items.HAY_BLOCK)
            .define('b', Items.POLISHED_BLACKSTONE)
            .define('q', Items.QUARTZ_SLAB)
            .define('i', Items.IRON_BARS)
            .unlockedBy("has_hay_block", has(Items.HAY_BLOCK))
            .save(output);

        // Interdiction Pylon
        shaped(ModRegistry.INTERDICTION_PYLON.get())
            .pattern("qqq")
            .pattern("idi")
            .pattern("bbb")
            .define('d', Items.NETHERITE_BLOCK)
            .define('b', Items.POLISHED_BLACKSTONE)
            .define('q', Items.QUARTZ_SLAB)
            .define('i', Items.IRON_BARS)
            .unlockedBy("has_netherite_block", has(Items.NETHERITE_BLOCK))
            .save(output);

        shaped(ModRegistry.MOB_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.INGOTS_NETHERITE)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_interdiction_pylon", has(ModRegistry.INTERDICTION_PYLON.get()))
            .save(output);

        shapeless(RecipeCategory.MISC, ModRegistry.MOB_FILTER.get())
            .group(Pylons.MODID)
            .requires(ModRegistry.MOB_FILTER.get())
            .unlockedBy("has_mob_filter", has(ModRegistry.MOB_FILTER.get()))
            .save(output, recipe("clear_mob_filter"));

        shaped(ModRegistry.LIFELESS_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.NETHER_STARS)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_interdiction_pylon", has(ModRegistry.INTERDICTION_PYLON.get()))
            .save(output);

        // Protection Pylon
        shaped(ModRegistry.PROTECTION_PYLON.get())
            .pattern("qqq")
            .pattern("idi")
            .pattern("bbb")
            .define('d', Items.HONEYCOMB_BLOCK)
            .define('b', Items.POLISHED_BLACKSTONE)
            .define('q', Items.QUARTZ_SLAB)
            .define('i', Items.IRON_BARS)
            .unlockedBy("has_honey_block", has(Items.HONEY_BLOCK))
            .save(output);

        shaped(ModRegistry.BLOCK_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Items.HONEYCOMB)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_protection_pylon", has(ModRegistry.PROTECTION_PYLON.get()))
            .save(output);

        harvestingRecipes(output);
    }

    protected void harvestingRecipes(RecipeOutput output) {
        HarvestingRecipeBuilder.forBlock(Blocks.SWEET_BERRY_BUSH)
            .setOutput(Items.SWEET_BERRIES, 2)
            .build(output);

        HarvestingRecipeBuilder.forBlock(Blocks.NETHER_WART)
            .setOutput(Items.NETHER_WART, 3)
            .build(output);
//
//        HarvestingRecipeBuilder.forBlock(BlockRegistry.SOURCEBERRY_BUSH.get())
//            .setOutput(BlockRegistry.SOURCEBERRY_BUSH.asItem(), 2)
//            .build(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        @Override
        public String getName() {
            return "pylons:crafting_recipes";
        }

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new CraftingRecipes(provider, output);
        }
    }
}
