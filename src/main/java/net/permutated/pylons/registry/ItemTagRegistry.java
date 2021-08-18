package net.permutated.pylons.registry;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import static net.permutated.pylons.util.ResourceUtil.forge;

public class ItemTagRegistry {
    private ItemTagRegistry() {
        // nothing to do
    }
    public static final Tags.IOptionalNamedTag<Item> ENDER_DUST = ItemTags.createOptional(forge("dusts/ender"));

    public static final ITag.INamedTag<Item> COAL_DUST = ItemTags.bind("forge:dusts/coal");
    public static final ITag.INamedTag<Item> FLUIX_DUST = ItemTags.bind("forge:dusts/fluix");
    public static final ITag.INamedTag<Item> SILICON = ItemTags.bind("forge:silicon");
}
