package net.permutated.pylons.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.permutated.pylons.data.client.BlockStates;
import net.permutated.pylons.data.client.ItemModels;
import net.permutated.pylons.data.client.Languages;
import net.permutated.pylons.data.server.BlockLoot;
import net.permutated.pylons.data.server.BlockTags;
import net.permutated.pylons.data.server.CraftingRecipes;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(true, new BlockTags(packOutput, event.getLookupProvider(), fileHelper));
            generator.addProvider(true, new CraftingRecipes(packOutput));
            generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK))));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new BlockStates(packOutput, fileHelper));
            generator.addProvider(true, new ItemModels(packOutput, fileHelper));
            generator.addProvider(true, new Languages.English(packOutput));
        }

    }
}
