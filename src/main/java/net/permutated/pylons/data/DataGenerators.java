package net.permutated.pylons.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;
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
            generator.addProvider(true, new BlockTags(generator, fileHelper));
            generator.addProvider(true, new CraftingRecipes(generator));
            generator.addProvider(true, new BlockLoot(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new BlockStates(generator, fileHelper));
            generator.addProvider(true, new ItemModels(generator, fileHelper));
            generator.addProvider(true, new Languages.English(generator));
        }

    }
}
