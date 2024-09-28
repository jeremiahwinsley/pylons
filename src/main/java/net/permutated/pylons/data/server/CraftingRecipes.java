package net.permutated.pylons.data.server;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.builder.HarvestingRecipeBuilder;

import java.util.concurrent.CompletableFuture;

import static net.permutated.pylons.util.ResourceUtil.prefix;

public class CraftingRecipes extends RecipeProvider {
    public CraftingRecipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider);
    }

    private ShapedRecipeBuilder shaped(ItemLike provider) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, provider)
            .group(Pylons.MODID);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {

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
            .save(consumer);

        shaped(ModRegistry.PLAYER_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.GEMS_DIAMOND)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_expulsion_pylon", has(ModRegistry.EXPULSION_PYLON_ITEM.get()))
            .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModRegistry.PLAYER_FILTER.get())
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
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_infusion_pylon", has(ModRegistry.INFUSION_PYLON_ITEM.get()))
            .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModRegistry.POTION_FILTER.get())
            .group(Pylons.MODID)
            .requires(ModRegistry.POTION_FILTER.get())
            .unlockedBy("has_potion_filter", has(ModRegistry.POTION_FILTER.get()))
            .save(consumer, prefix("clear_potion_filter"));

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
            .save(consumer);

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
            .save(consumer);

        shaped(ModRegistry.MOB_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.INGOTS_NETHERITE)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_interdiction_pylon", has(ModRegistry.INTERDICTION_PYLON.get()))
            .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModRegistry.MOB_FILTER.get())
            .group(Pylons.MODID)
            .requires(ModRegistry.MOB_FILTER.get())
            .unlockedBy("has_mob_filter", has(ModRegistry.MOB_FILTER.get()))
            .save(consumer, prefix("clear_mob_filter"));

        shaped(ModRegistry.LIFELESS_FILTER.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.NETHER_STARS)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_interdiction_pylon", has(ModRegistry.INTERDICTION_PYLON.get()))
            .save(consumer);

        harvestingRecipes(consumer);
    }

    protected void harvestingRecipes(RecipeOutput consumer) {
        HarvestingRecipeBuilder.forBlock(Blocks.SWEET_BERRY_BUSH)
            .setOutput(Items.SWEET_BERRIES, 2)
            .build(consumer);

        HarvestingRecipeBuilder.forBlock(Blocks.NETHER_WART)
            .setOutput(Items.NETHER_WART, 3)
            .build(consumer);

        HarvestingRecipeBuilder.forBlock(BlockRegistry.SOURCEBERRY_BUSH.get())
            .setOutput(BlockRegistry.SOURCEBERRY_BUSH.asItem(), 2)
            .build(consumer);
    }
}
