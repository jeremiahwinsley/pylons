package net.permutated.pylons.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.Pylons;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = Pylons.MODID)
public class ChunkManager {
    private static final Map<UUID, Location> loaded = new ConcurrentHashMap<>();
    private static final Map<UUID, Location> unloaded = new ConcurrentHashMap<>();
    private static final TicketController controller = new TicketController(ResourceUtil.prefix("block"), ChunkManager::validateTickets);

    private ChunkManager() {
        // nothing to do
    }

    public static void onRegisterTicketControllersEvent(RegisterTicketControllersEvent event) {
        event.register(controller);
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
        var uuid = event.getEntity().getUUID();
        if (uuid != null && unloaded.containsKey(uuid)) {
            var location = unloaded.get(uuid);
            var server = event.getEntity().getServer();
            if (location != null && server != null) {
                var level = server.getLevel(location.level());
                if (level != null) {
                    loadChunk(uuid, level, location.blockPos());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        var uuid = event.getEntity().getUUID();
        if (uuid != null && loaded.containsKey(uuid)) {
            var location = loaded.get(uuid);
            var server = event.getEntity().getServer();
            if (location != null && server != null) {
                var level = server.getLevel(location.level());
                if (level != null) {
                    unloadChunk(uuid, level, location.blockPos());
                }
            }
        }
    }

    @SuppressWarnings("java:S3824") // containsKey cannot be replaced with computeIfAbsent
    public static void loadChunk(UUID owner, ServerLevel level, BlockPos pos) {
        if (Boolean.TRUE.equals(ConfigManager.SERVER.infusionChunkloads.get()) && !loaded.containsKey(owner)) {
            var location = Location.of(level, pos);
            unloaded.remove(owner, location);
            loaded.put(owner, location);
            controller.forceChunk(level, pos, location.chunkX(), location.chunkZ(), true, false);
        }
    }

    public static void unloadChunk(UUID owner, ServerLevel level, BlockPos pos) {
        var location = Location.of(level, pos);
        loaded.remove(owner, location);
        unloaded.put(owner, location);
        controller.forceChunk(level, pos, location.chunkX(), location.chunkZ(), false, false);
    }

    @SuppressWarnings("java:S1172") // unused parameter is required
    public static void validateTickets(ServerLevel level, TicketHelper ticketHelper) {
        ticketHelper.getBlockTickets().keySet().forEach(ticketHelper::removeAllTickets);
    }
}
