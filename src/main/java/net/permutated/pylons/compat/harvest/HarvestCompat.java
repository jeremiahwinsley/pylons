package net.permutated.pylons.compat.harvest;

import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.permutated.pylons.compat.harvest.adapters.CobblemonBerryBush;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HarvestCompat {
    private static final Map<Class<? extends Block>, Harvestable> compat = new ConcurrentHashMap<>();

    private HarvestCompat() {
        // nothing to do
    }

    private static void register(Harvestable harvestable) {
        compat.put(harvestable.getBlock(), harvestable);
    }

    public static Harvestable getCompat(Block block) {
        return compat.get(block.getClass());
    }

    public static boolean hasCompat(Block block) {
        return compat.containsKey(block.getClass());
    }

    // FMLCommonSetupEvent
    public static void init() {
        if (ModList.get().isLoaded("cobblemon")) {
            register(new CobblemonBerryBush());
        }
    }
}
