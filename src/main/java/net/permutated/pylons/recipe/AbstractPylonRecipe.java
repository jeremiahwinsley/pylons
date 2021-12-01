package net.permutated.pylons.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class AbstractPylonRecipe implements Recipe<Container> {

    private final ResourceLocation id;

    protected AbstractPylonRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return true;
    }

    @Override
    public ItemStack assemble(Container inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    public abstract void write(FriendlyByteBuf buffer);

    protected abstract static class AbstractSerializer<T extends AbstractPylonRecipe>
        extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    }
}
