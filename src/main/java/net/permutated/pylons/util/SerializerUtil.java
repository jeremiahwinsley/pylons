package net.permutated.pylons.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.JSONUtils;

import java.util.Objects;

public class SerializerUtil {
    private SerializerUtil() {
        // nothing to do
    }

    private static void validateKey(JsonObject json, String key)
    {
        if (!json.has(key))
        {
            throw new JsonSyntaxException("Missing '" + key + "', expected to find an object");
        }
        if (!json.get(key).isJsonObject())
        {
            throw new JsonSyntaxException("Expected '" + key + "' to be an object");
        }
    }

    public static ItemStack getItemStack(JsonObject json, String key)
    {
        validateKey(json, key);
        return ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, key));
    }

    public static JsonElement serializeItemStack(ItemStack stack)
    {
        JsonObject json = new JsonObject();
        json.addProperty(Constants.JSON.ITEM, Objects.requireNonNull(stack.getItem().getRegistryName()).toString());
        if (stack.getCount() > 1)
        {
            json.addProperty(Constants.JSON.COUNT, stack.getCount());
        }
        if (stack.hasTag())
        {
            json.addProperty(Constants.JSON.NBT, stack.getOrCreateTag().toString());
        }
        return json;
    }
}
