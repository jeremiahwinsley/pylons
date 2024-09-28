package net.permutated.pylons.builder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.recipe.HarvestingRecipe;
import net.permutated.pylons.util.Constants;

public class HarvestingRecipeBuilder {
    private final Block input;
    private ItemStack output;

    protected String getPrefix() {
        return Constants.HARVESTING;
    }

    public HarvestingRecipeBuilder(Block input) {
        this.input = input;
    }

    public static HarvestingRecipeBuilder forBlock(Block block) {
        return new HarvestingRecipeBuilder(block);
    }

    public HarvestingRecipeBuilder setOutput(Item item, int count) {
        this.output = new ItemStack(item, count);
        return this;
    }

    public void build(RecipeOutput consumer) {
        String modId = BuiltInRegistries.BLOCK.getKey(input).getNamespace();
        String path = BuiltInRegistries.BLOCK.getKey(input).getPath();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Pylons.MODID, getPrefix() + "/" + modId + "/" + path);

        if (output == null) {
            throw new RecipeException(id.toString(), "output is required");
        }

        consumer
            .withConditions(new ModLoadedCondition(modId))
            .accept(id, new HarvestingRecipe(input, output), null);
    }
}
