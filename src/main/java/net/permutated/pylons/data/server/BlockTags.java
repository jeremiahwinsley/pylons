package net.permutated.pylons.data.server;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.block.AbstractPylonBlock;

import javax.annotation.Nullable;

import static net.permutated.pylons.util.ResourceUtil.blockTag;

public class BlockTags extends BlockTagsProvider {
    public BlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, Pylons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        Block[] pylons = ModRegistry.BLOCKS.getEntries().stream()
            .map(RegistryObject::get)
            .filter(AbstractPylonBlock.class::isInstance)
            .toArray(Block[]::new);

        tag(blockTag("minecraft:mineable/pickaxe")).add(pylons);
        tag(blockTag("create:brittle")).add(pylons);
    }
}
