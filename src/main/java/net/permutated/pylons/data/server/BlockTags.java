package net.permutated.pylons.data.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.machines.base.AbstractPylonBlock;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.permutated.pylons.util.ResourceUtil.blockTag;

public class BlockTags extends BlockTagsProvider {
    public BlockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, Pylons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Block[] pylons = ModRegistry.BLOCKS.getEntries().stream()
            .map(Supplier::get)
            .filter(AbstractPylonBlock.class::isInstance)
            .toArray(Block[]::new);

        tag(blockTag("minecraft:mineable/pickaxe")).add(pylons);
        tag(blockTag("create:brittle")).add(pylons);
    }
}
