package net.permutated.pylons.compat.harvest;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.permutated.pylons.compat.harvest.adapters.ArsNouveauSourceBerryBush;
import net.permutated.pylons.compat.harvest.adapters.MinecraftNetherWart;
import net.permutated.pylons.compat.harvest.adapters.MinecraftSweetBerryBush;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HarvestCompat {
    private static final Map<Block, Harvestable> compat = new ConcurrentHashMap<>();

    private HarvestCompat() {
        // nothing to do
    }

    private static void register(Harvestable harvestable) {
        compat.put(harvestable.getBlock(), harvestable);
    }

    public static Harvestable getCompat(Block block) {
        return compat.get(block);
    }

    public static boolean hasCompat(Block block) {
        return compat.containsKey(block);
    }

    // FMLCommonSetupEvent
    public static void init() {
        register(new MinecraftNetherWart());
        register(new MinecraftSweetBerryBush());
        if (ModList.get().isLoaded("ars_nouveau")) {
            register(new ArsNouveauSourceBerryBush());
        }
    }
}
