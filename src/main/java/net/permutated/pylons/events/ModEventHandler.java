package net.permutated.pylons.events;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.recipe.HarvestingRecipe;

import java.util.List;

@EventBusSubscriber(modid = Pylons.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEventHandler {
    private ModEventHandler() {
        // nothing to do
    }

    public static void loadHarvestingRecipes(RecipeManager recipeManager) {
        List<HarvestingRecipe> harvestingRecipes = recipeManager
            .getAllRecipesFor(ModRegistry.HARVESTING_RECIPE_TYPE.get())
            .stream().map(RecipeHolder::value).toList();
        ModRegistry.HARVESTING_REGISTRY.setRecipeList(harvestingRecipes);
        Pylons.LOGGER.debug("Registered {} harvesting recipes", harvestingRecipes.size());
    }

    @SubscribeEvent
    public static void onRecipesUpdatedEvent(final RecipesUpdatedEvent event) {
        Pylons.LOGGER.debug("Loading recipes on server sync");
        loadHarvestingRecipes(event.getRecipeManager());
    }

    @SubscribeEvent
    public static void onServerStartingEvent(final ServerStartingEvent event) {
        if (event.getServer().isDedicatedServer()) {
            Pylons.LOGGER.debug("Loading recipes on server startup");
            loadHarvestingRecipes(event.getServer().getRecipeManager());
        }
    }
}
