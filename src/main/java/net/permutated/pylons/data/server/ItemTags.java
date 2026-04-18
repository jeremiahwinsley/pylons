package net.permutated.pylons.data.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

import java.util.concurrent.CompletableFuture;

public class ItemTags extends ItemTagsProvider {
    public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Pylons.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateRawBuilder(ModRegistry.HARVESTER_BANNED)
            .addOptionalElement(Identifier.parse("silentgear:hoe"));
    }
}
