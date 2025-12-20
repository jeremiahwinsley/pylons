package net.permutated.pylons.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.permutated.pylons.Pylons;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = Pylons.MODID)
public class ProtectionManager {
    private ProtectionManager() {
        // nothing to do
    }

    public interface Filter {
        boolean matches(UUID owner, ResourceLocation location);
    }

    public static Filter mobFilter(UUID owner, ResourceLocation location) {
        return new MobFilter(owner, location);
    }

    public static Filter blockFilter(UUID owner, ResourceLocation location) {
        return new BlockFilter(owner, location);
    }

    record MobFilter(UUID owner, ResourceLocation filter) implements Filter {
        @Override
        public boolean matches(UUID player, ResourceLocation location) {
            return owner.equals(player) && filter.equals(location);
        }
    }

    record BlockFilter(UUID owner, ResourceLocation filter) implements Filter {
        @Override
        public boolean matches(UUID player, ResourceLocation location) {
            return owner.equals(player) && filter.equals(location);
        }
    }

    private static boolean dirty = false;
    // All chunks in range of a pylon, mapped to a set of entity or block IDs to protect
    private static Map<Location, Set<Filter>> chunkMap = new ConcurrentHashMap<>();
    // All pylon locations, mapped to a set of chunks and a set of entity or block IDs
    private static final Map<Location, Pair<Set<Location>, Set<Filter>>> pylonMap = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (dirty) {
            Map<Location, Set<Filter>> replace = new ConcurrentHashMap<>();

            // given a set of Locations, apply the filter set to each Location
            pylonMap.values().forEach(pair -> pair.getLeft()
                .forEach(location -> replace.merge(location, pair.getRight(), Sets::union)));

            chunkMap = replace;
            dirty = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onAttackEntityEvent(AttackEntityEvent event) {
        if (event.getTarget() instanceof LivingEntity entity && entity.level() instanceof ServerLevel level) {
            int chunkX = SectionPos.posToSectionCoord(entity.getX());
            int chunkZ = SectionPos.posToSectionCoord(entity.getZ());

            Location location = new Location(level.dimension(), BlockPos.ZERO, chunkX, chunkZ);
            Set<Filter> filterSet = chunkMap.get(location);
            if (filterSet == null) return;

            UUID player = event.getEntity().getUUID();
            ResourceLocation target = BuiltInRegistries.ENTITY_TYPE.getKey(event.getTarget().getType());
            for (Filter filter : filterSet) {
                if (filter instanceof MobFilter && filter.matches(player, target)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level) {
            int chunkX = SectionPos.posToSectionCoord(event.getEntity().getX());
            int chunkZ = SectionPos.posToSectionCoord(event.getEntity().getZ());

            Location location = new Location(level.dimension(), BlockPos.ZERO, chunkX, chunkZ);
            Set<Filter> filterSet = chunkMap.get(location);
            if (filterSet == null) return;

            DamageSource source = event.getSource();
            Entity entity = source.getEntity() == null ? source.getDirectEntity() : source.getEntity();

            if (entity instanceof Player) {
                UUID player = entity.getUUID();
                ResourceLocation target = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType());
                for (Filter filter : filterSet) {
                    if (filter instanceof MobFilter && filter.matches(player, target)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            int chunkX = SectionPos.posToSectionCoord(event.getPos().getX());
            int chunkZ = SectionPos.posToSectionCoord(event.getPos().getZ());

            Location location = new Location(level.dimension(), BlockPos.ZERO, chunkX, chunkZ);
            Set<Filter> filterSet = chunkMap.get(location);
            if (filterSet == null) return;

            UUID player = event.getPlayer().getUUID();
            ResourceLocation target = BuiltInRegistries.BLOCK.getKey(level.getBlockState(event.getPos()).getBlock());
            for (Filter filter : filterSet) {
                if (filter instanceof BlockFilter && filter.matches(player, target)) {
                    event.setCanceled(true);
                }
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
    public static void register(ServerLevel level, BlockPos blockPos, Range range, Collection<Filter> filters) {
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
