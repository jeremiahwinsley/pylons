package net.permutated.pylons.registry;

import net.minecraft.item.crafting.IRecipeType;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.recipe.BeaconEffectRecipe;

import static net.minecraft.item.crafting.IRecipeType.register;
import static net.permutated.pylons.util.ResourceUtil.id;

public class RecipeTypeRegistry {
    private RecipeTypeRegistry() {
        // nothing to do
    }

    public static final IRecipeType<BeaconEffectRecipe> BEACON_EFFECT = register(id(Constants.BEACON_PYLON));



}
