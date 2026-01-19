package net.permutated.pylons.compat.dye;

import cy.jdkdigital.dyenamics.core.util.DyenamicDyeColor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class Dyenamics implements DyeResolver {
    public @Nullable Integer getColor(@Nonnull ItemStack stack) {
        DyenamicDyeColor color = DyenamicDyeColor.getColor(stack);
        return color == null ? null : color.getColorValue();
    }
}
