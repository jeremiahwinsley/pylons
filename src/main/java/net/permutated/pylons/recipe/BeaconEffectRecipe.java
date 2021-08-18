package net.permutated.pylons.recipe;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.registry.RecipeTypeRegistry;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.SerializerUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeaconEffectRecipe extends AbstractPylonRecipe {

    private final List<Ingredient> input = new ArrayList<>();
    private final ItemStack output;

    public BeaconEffectRecipe(ResourceLocation id, List<Ingredient> input, ItemStack output) {
        super(id);
        Preconditions.checkNotNull(input, "input cannot be null.");
        Preconditions.checkArgument(!input.isEmpty(), "input cannot be empty");
        Preconditions.checkArgument(input.size() <= 3, "input has too many ingredients");

        Preconditions.checkNotNull(output, "output cannot be null.");

        this.input.addAll(input);
        this.output = output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRegistry.BEACON_EFFECT_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypeRegistry.BEACON_EFFECT;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeItem(output);
        buffer.writeInt(input.size());
        input.forEach(ingredient -> ingredient.toNetwork(buffer));
    }

    public List<Ingredient> getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public static class Serializer extends AbstractSerializer<BeaconEffectRecipe> {
        @Override
        public BeaconEffectRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            ItemStack output = SerializerUtil.getItemStack(jsonObject, Constants.JSON.OUTPUT);

            List<Ingredient> input = new ArrayList<>();
            jsonObject.getAsJsonArray(Constants.JSON.INPUT)
                .forEach(jsonElement -> input.add(Ingredient.fromJson(jsonElement)));

            return new BeaconEffectRecipe(resourceLocation, input, output);
        }

        @Nullable
        @Override
        public BeaconEffectRecipe fromNetwork(ResourceLocation resourceLocation, PacketBuffer packetBuffer) {
            ItemStack output = packetBuffer.readItem();
            List<Ingredient> input = new ArrayList<>();

            int size = packetBuffer.readInt();
            for (int i = 0; i < size; i++) {
                input.add(Ingredient.fromNetwork(packetBuffer));
            }

            return new BeaconEffectRecipe(resourceLocation, input, output);
        }

        @Override
        public void toNetwork(PacketBuffer packetBuffer, BeaconEffectRecipe aggregatorRecipe) {
            aggregatorRecipe.write(packetBuffer);
        }
    }
}
