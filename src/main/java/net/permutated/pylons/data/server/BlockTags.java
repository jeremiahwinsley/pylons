package net.permutated.pylons.data.server;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.permutated.pylons.Pylons;

import javax.annotation.Nullable;

public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, Pylons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // nothing to do
    }
}

