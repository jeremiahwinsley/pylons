package net.permutated.pylons.util;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.permutated.pylons.Pylons;

public class ResourceUtil {
    private ResourceUtil() {
        // nothing to do
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(Pylons.MODID, path);
    }

    public static String id(String path) {
        return prefix(path).toString();
    }

    public static ResourceLocation forge(String path) {
        return new ResourceLocation("forge", path);
    }

    public static ITag<Item> tag(String path) {
        return ItemTags.createOptional(new ResourceLocation(path));
    }

    public static ResourceLocation gui(String path) {
        return prefix(String.format("textures/gui/%s.png", path));
    }
    public static ResourceLocation jei(String path) {
        return prefix(String.format("textures/jei/%s.png", path));
    }
}
