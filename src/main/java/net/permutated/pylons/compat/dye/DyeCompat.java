package net.permutated.pylons.compat.dye;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class DyeCompat {
    private static final List<DyeResolver> resolvers = new ArrayList<>();

    public static void init() {
        if (ModList.get().isLoaded("dyenamics")) {
            resolvers.add(new Dyenamics());
        }
    }

    private static int vanilla(ItemStack stack) {
        DyeColor color = DyeColor.getColor(stack);
        return color == null ? -1 : color.getTextureDiffuseColor();
    }

    public static int getColor(ItemStack stack) {
        for (DyeResolver resolver : resolvers) {
            Integer result = resolver.getColor(stack);
            if (result != null) {
                return result;
            }
        }
        return DyeCompat.vanilla(stack);
    }
}
