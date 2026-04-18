package net.permutated.pylons.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.permutated.pylons.Pylons;

public class ResourceUtil {
    private ResourceUtil() {
        // nothing to do
    }

    public static ResourceKey<Recipe<?>> recipe(String path) {
        return ResourceKey.create(Registries.RECIPE, prefix(path));
    }

    public static Identifier prefix(String path) {
        return Identifier.fromNamespaceAndPath(Pylons.MODID, path);
    }

    public static String id(String path) {
        return prefix(path).toString();
    }

    public static Identifier forge(String path) {
        return Identifier.fromNamespaceAndPath("forge", path);
    }

    public static Identifier c(String path) {
        return Identifier.fromNamespaceAndPath("c", path);
    }

    public static TagKey<Item> tag(String path) {
        return ItemTags.create(Identifier.parse(path));
    }

    public static TagKey<Block> blockTag(String path) {
        return BlockTags.create(Identifier.parse(path));
    }

    public static Identifier gui(String path) {
        return prefix(String.format("textures/gui/%s.png", path));
    }
    public static Identifier jei(String path) {
        return prefix(String.format("textures/jei/%s.png", path));
    }
}
