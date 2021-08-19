package net.permutated.pylons.data.server;


import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraftforge.common.Tags;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.data.builder.BeaconEffectRecipeBuilder;
import net.permutated.pylons.registry.ItemTagRegistry;

import java.util.function.Consumer;

public class BeaconEffectRecipes extends RecipeProvider {
    public BeaconEffectRecipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        // Steel Processing
        BeaconEffectRecipeBuilder.builder(ModRegistry.PLAYER_FILTER.get())
            .addInput(ItemTagRegistry.COAL_DUST)
            .addInput(ItemTagRegistry.FLUIX_DUST)
            .addInput(Tags.Items.INGOTS_IRON)
            .build(consumer);
    }

    @Override
    public String getName() {
        return "Beacon Effect Recipes";
    }
}
