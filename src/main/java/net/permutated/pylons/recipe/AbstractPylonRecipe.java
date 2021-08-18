package net.permutated.pylons.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class AbstractPylonRecipe implements IRecipe<IInventory> {

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
    public boolean matches(IInventory inventory, World world) {
        return true;
    }

    @Override
    public ItemStack assemble(IInventory inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    public abstract void write(PacketBuffer buffer);

    protected abstract static class AbstractSerializer<T extends AbstractPylonRecipe>
        extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    }
}
