package net.permutated.pylons.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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

    public static TagKey<Item> tag(String path) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(path));
    }

    public static TagKey<Block> blockTag(String path) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(path));
    }

    public static ResourceLocation gui(String path) {
        return prefix(String.format("textures/gui/%s.png", path));
    }
    public static ResourceLocation jei(String path) {
        return prefix(String.format("textures/jei/%s.png", path));
    }
}
