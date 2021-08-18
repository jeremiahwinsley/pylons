package net.permutated.pylons.data.server;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.registry.ItemTagRegistry;

import static net.permutated.pylons.util.ResourceUtil.fromAE2;

public class ItemTags extends ItemTagsProvider {
    public ItemTags(DataGenerator generator, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, Pylons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // nothing to do
    }
}
