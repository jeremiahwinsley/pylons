package net.permutated.pylons.recipe;


import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HarvestingRegistry {
    private final Object lock = new Object();
    private List<HarvestingRecipe> recipeList = new ArrayList<>();
    private final Map<Block, Optional<HarvestingRecipe>> recipeByBlockCache = new ConcurrentHashMap<>();

    public Optional<HarvestingRecipe> findRecipe(final Block block) {
        return recipeByBlockCache.computeIfAbsent(block, b -> recipeList.stream()
            .filter(recipe -> recipe.getInput().equals(b))
            .findFirst());
    }

    public boolean hasRecipe(final Block block) {
        return findRecipe(block).isPresent();
    }

    public void setRecipeList(final List<HarvestingRecipe> recipes) {
        synchronized (lock) {
            recipeList = List.copyOf(recipes);
            recipeByBlockCache.clear();
        }
    }

    public List<HarvestingRecipe> getRecipeList() {
        synchronized (lock) {
            return recipeList;
        }
    }

    public void clearRecipes() {
        synchronized (lock) {
            recipeList = Collections.emptyList();
            recipeByBlockCache.clear();
        }
    }
}
