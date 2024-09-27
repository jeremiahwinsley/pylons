package net.permutated.pylons.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.permutated.pylons.Pylons;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = Pylons.MODID)
public class SpawnManager {
    private SpawnManager() {
        // nothing to do
    }

    private static boolean dirty = false;
    // All chunks in range of a pylon, mapped to a set of entity IDs to block
    private static Map<Location, Set<ResourceLocation>> chunkMap = new ConcurrentHashMap<>();
    // All pylon locations, mapped to a set of chunks and a set of entity IDs
    private static final Map<Location, Pair<Set<Location>, Set<ResourceLocation>>> pylonMap = new ConcurrentHashMap<>();
    // Spawn types that will be blocked by the lifeless filter
    private static final List<MobSpawnType> blockedSpawnTypes = List.of(MobSpawnType.NATURAL, MobSpawnType.STRUCTURE);

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (dirty) {
            Map<Location, Set<ResourceLocation>> replace = new ConcurrentHashMap<>();
            // given a set of Locations, apply the filter set to each Location
            pylonMap.values().forEach(pair -> pair.getLeft()
                .forEach(location -> replace.merge(location, pair.getRight(), Sets::union)));
            chunkMap = replace;
            dirty = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinWorldEvent(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof LivingEntity entity) {
            int chunkX = SectionPos.posToSectionCoord(entity.getX());
            int chunkZ = SectionPos.posToSectionCoord(entity.getZ());

            Location location = new Location(level.dimension(), BlockPos.ZERO, chunkX, chunkZ);
            Set<ResourceLocation> filterSet = chunkMap.get(location);
            if (filterSet == null) return;

            ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType());
            if (filterSet.contains(key)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMobSpawnEvent(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getLevel() instanceof ServerLevel level && blockedSpawnTypes.contains(event.getSpawnType())) {
            int chunkX = SectionPos.posToSectionCoord(event.getPos().getX());
            int chunkZ = SectionPos.posToSectionCoord(event.getPos().getZ());

            Location location = new Location(level.dimension(), BlockPos.ZERO, chunkX, chunkZ);
            Set<ResourceLocation> filterSet = chunkMap.get(location);
            if (filterSet == null) return;

            if (filterSet.isEmpty()) {
                event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
            }
        }
    }

    public static void registerLifeless(ServerLevel level, BlockPos blockPos) {
        Location pylon = Location.of(level, blockPos);

        // mobs can spawn up to 128 blocks / 8 chunks away from the player
        // add 50% to account for player moving around, for a total size of 25x25 chunks covered
        Range range = new Range(new byte[]{25});

        Set<Location> locations = Location.chunkSet(level, blockPos, range);
        pylonMap.put(pylon, Pair.of(locations, Set.of()));
        dirty = true;
    }

    /**
     * Called when the pylon is updated to refresh the loaded chunks.
     *
     * @param level    the dimension where the pylon is located
     * @param blockPos the location of the pylon
     * @param range    the current range setting of the pylon
     * @param filters  the list of filters currently in the pylon
     */
    public static void register(ServerLevel level, BlockPos blockPos, Range range, Collection<ResourceLocation> filters) {
        if (filters.isEmpty()) {
            unregister(level, blockPos);
            return;
        }

        Location pylon = Location.of(level, blockPos);
        Set<Location> locations = Location.chunkSet(level, blockPos, range);
        pylonMap.put(pylon, Pair.of(locations, ImmutableSet.copyOf(filters)));
        dirty = true;
    }

    public static void unregister(ServerLevel level, BlockPos blockPos) {
        Location pylon = Location.of(level, blockPos);
        pylonMap.remove(pylon);
        dirty = true;
    }
}
