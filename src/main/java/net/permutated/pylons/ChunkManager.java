package net.permutated.pylons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Pylons.MODID)
public class ChunkManager {
    private static final Map<UUID, Location> loaded = new ConcurrentHashMap<>();
    private static final Map<UUID, Location> unloaded = new ConcurrentHashMap<>();

    private ChunkManager() {
        // nothing to do
    }

    @SubscribeEvent
    public static void beforeServerStopped(ServerStoppingEvent event) {
        loaded.clear();
        unloaded.clear();
    }

    @SubscribeEvent
    public static void afterServerStarted(ServerStartedEvent event) {
        loaded.clear();
        unloaded.clear();
    }

    @SubscribeEvent
    public static void onLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        var uuid = event.getPlayer().getUUID();
        if (uuid != null && unloaded.containsKey(uuid)) {
            var location = unloaded.get(uuid);
            var server = event.getPlayer().getServer();
            if (location != null && server != null) {
                var level = server.getLevel(location.level);
                loadChunk(uuid, level, location.blockPos);
            }
        }
    }

    @SubscribeEvent
    public static void onLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        var uuid = event.getPlayer().getUUID();
        if (uuid != null && loaded.containsKey(uuid)) {
            var location = loaded.get(uuid);
            var server = event.getPlayer().getServer();
            if (location != null && server != null) {
                var level = server.getLevel(location.level);
                unloadChunk(uuid, level, location.blockPos);
            }
        }
    }

    @SuppressWarnings("java:S3824") // containsKey cannot be replaced with computeIfAbsent
    public static void loadChunk(UUID owner, ServerLevel level, BlockPos pos) {
        if (Boolean.TRUE.equals(ConfigManager.COMMON.infusionChunkloads.get()) && !loaded.containsKey(owner)) {
            var location = Location.of(level, pos);
            unloaded.remove(owner, location);
            loaded.put(owner, location);
            ForgeChunkManager.forceChunk(level, Pylons.MODID, pos, location.chunkX, location.chunkZ, true, false);
        }
    }

    public static void unloadChunk(UUID owner, ServerLevel level, BlockPos pos) {
        var location = Location.of(level, pos);
        loaded.remove(owner, location);
        unloaded.put(owner, location);
        ForgeChunkManager.forceChunk(level, Pylons.MODID, pos, location.chunkX, location.chunkZ, false, false);
    }

    @SuppressWarnings("java:S1172") // unused parameter is required
    public static void validateTickets(ServerLevel level, ForgeChunkManager.TicketHelper ticketHelper) {
        ticketHelper.getBlockTickets().keySet().forEach(ticketHelper::removeAllTickets);
    }

    private record Location(ResourceKey<Level> level, BlockPos blockPos, int chunkX, int chunkZ) {
        static Location of(ServerLevel level, BlockPos blockPos) {
            var chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
            var chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());
            return new Location(level.dimension(), blockPos, chunkX, chunkZ);
        }
    }
}
