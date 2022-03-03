package net.permutated.pylons.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.permutated.pylons.data.client.BlockStates;
import net.permutated.pylons.data.client.ItemModels;
import net.permutated.pylons.data.client.Languages;
import net.permutated.pylons.data.server.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(new BlockTags(generator, fileHelper));
            generator.addProvider(new CraftingRecipes(generator));
            generator.addProvider(new LootTables(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new BlockStates(generator, fileHelper));
            generator.addProvider(new ItemModels(generator, fileHelper));
            generator.addProvider(new Languages.English(generator));
        }

    }
}
