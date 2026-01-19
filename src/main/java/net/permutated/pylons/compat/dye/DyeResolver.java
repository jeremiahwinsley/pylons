package net.permutated.pylons.compat.dye;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DyeResolver {
    /**
     * Retrieve a color, falling back to vanilla DyeColor
     * @param stack the input stack
     * @return resolved color, or null if unable to resolve
     */
    @Nullable Integer getColor(@Nonnull ItemStack stack);
}
