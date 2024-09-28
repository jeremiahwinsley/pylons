package net.permutated.pylons.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;

public record HarvestingRecipeInput(Block block) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("No item for index " + index);
        } else {
            return new ItemStack(block.asItem());
        }
    }

    @Override
    public int size() {
        return 1;
    }
}
