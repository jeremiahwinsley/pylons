package net.permutated.pylons.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.ResourceUtil;

import static net.permutated.pylons.util.TranslationKey.translateJei;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceUtil.prefix("jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(new ItemStack(ModRegistry.EXPULSION_PYLON.get()), VanillaTypes.ITEM_STACK, translateJei(Constants.EXPULSION_PYLON));
        registration.addIngredientInfo(new ItemStack(ModRegistry.HARVESTER_PYLON.get()), VanillaTypes.ITEM_STACK, translateJei(Constants.HARVESTER_PYLON));
        registration.addIngredientInfo(new ItemStack(ModRegistry.INFUSION_PYLON.get()), VanillaTypes.ITEM_STACK, translateJei(Constants.INFUSION_PYLON));
        registration.addIngredientInfo(new ItemStack(ModRegistry.INTERDICTION_PYLON.get()), VanillaTypes.ITEM_STACK, translateJei(Constants.INTERDICTION_PYLON));
    }
}
