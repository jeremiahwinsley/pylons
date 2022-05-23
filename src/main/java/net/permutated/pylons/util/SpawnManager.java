package net.permutated.pylons.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.permutated.pylons.Pylons;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Pylons.MODID)
public class SpawnManager {
    private SpawnManager() {
        // nothing to do
    }

    private static boolean dirty = false;
    // All chunks in range of a pylon, mapped to a set of entity IDs to block
    private static Map<Location, Set<String>> chunkMap = new ConcurrentHashMap<>();
    // All pylon locations, mapped to a set of chunks and a set of entity IDs
    private static final Map<Location, Pair<Set<Location>, Set<String>>> pylonMap = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END) && dirty) {
            Map<Location, Set<String>> replace = new ConcurrentHashMap<>();
            // given a set of Locations, apply the filter set to each Location
            pylonMap.values().forEach(pair -> pair.getLeft()
                .forEach(location -> replace.merge(location, pair.getRight(), Sets::union)));
            chunkMap = replace;
            dirty = false;
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getWorld() instanceof ServerWorld && event.getEntity() instanceof LivingEntity) {
            ServerWorld level = (ServerWorld) event.getWorld();
            LivingEntity entity = (LivingEntity) event.getEntity();

            int chunkX = SectionPos.blockToSectionCoord(MathHelper.floor(entity.getX()));
            int chunkZ = SectionPos.blockToSectionCoord(MathHelper.floor(entity.getZ()));

            Location key = new Location(level.dimension(), BlockPos.ZERO, chunkX, chunkZ);
            Set<String> filterSet = chunkMap.get(key);
            if (filterSet == null) return;

            String registryId = Objects.toString(event.getEntity().getType().getRegistryName(), "unregistered");
            if (filterSet.contains(registryId)) {
                event.setCanceled(true);
            }
        }
    }

    /**
     * Called when the pylon is updated to refresh the loaded chunks.
     *
     * @param level    the dimension where the pylon is located
     * @param blockPos the location of the pylon
     * @param range    the current range setting of the pylon
     * @param filters  the list of filters currently in the pylon
     */
    public static void register(ServerWorld level, BlockPos blockPos, Range range, Collection<String> filters) {
        if (filters.isEmpty()) return;

        Location pylon = Location.of(level, blockPos);
        Set<Location> locations = Location.chunkSet(level, blockPos, range);
        pylonMap.put(pylon, Pair.of(locations, ImmutableSet.copyOf(filters)));
        dirty = true;
    }

    public static void unregister(ServerWorld level, BlockPos blockPos) {
        Location pylon = Location.of(level, blockPos);
        pylonMap.remove(pylon);
        dirty = true;
    }
}
