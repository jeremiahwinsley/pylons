package net.permutated.pylons.events;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.recipe.HarvestingRecipe;
import net.permutated.pylons.util.ResourceUtil;

import java.util.List;

@EventBusSubscriber(modid = Pylons.MODID)
public class ModEventHandler {
    private ModEventHandler() {
        // nothing to do
    }

    public static void loadHarvestingRecipes(RecipeManager recipeManager) {
        List<HarvestingRecipe> harvestingRecipes = recipeManager.recipeMap()
            .byType(ModRegistry.HARVESTING_RECIPE_TYPE.get())
            .stream().map(RecipeHolder::value).toList();
        ModRegistry.HARVESTING_REGISTRY.setRecipeList(harvestingRecipes);
        Pylons.LOGGER.debug("Registered {} harvesting recipes", harvestingRecipes.size());
    }

    @SubscribeEvent
    public static void onRecipesUpdatedEvent(final AddServerReloadListenersEvent event) {
        event.addListener(ResourceUtil.prefix("recipes_updated"), new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                Pylons.LOGGER.debug("Loading recipes on server sync");
                loadHarvestingRecipes(event.getServerResources().getRecipeManager());
            }
        });
    }

    @SubscribeEvent
    public static void onServerStoppingEvent(final ServerStoppingEvent event) {
        if (event.getServer().isSingleplayer()) {
            Pylons.LOGGER.debug("Clearing recipe cache after logging out");
            ModRegistry.HARVESTING_REGISTRY.clearRecipes();
        }
    }
}
