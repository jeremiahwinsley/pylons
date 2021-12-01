package net.permutated.pylons.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

import javax.annotation.Nullable;

import static net.permutated.pylons.util.ResourceUtil.blockTag;

public class BlockTags extends BlockTagsProvider {
    public BlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, Pylons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(blockTag("minecraft:mineable/pickaxe")).add(
            ModRegistry.EXPULSION_PYLON.get(),
            ModRegistry.INFUSION_PYLON.get()
        );
    }
}
