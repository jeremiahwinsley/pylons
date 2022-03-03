package net.permutated.pylons.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.Pylons;

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
    public static void beforeServerStopped(FMLServerStoppedEvent event) {
        loaded.clear();
        unloaded.clear();
    }

    @SubscribeEvent
    public static void afterServerStarted(FMLServerStartedEvent event) {
        loaded.clear();
        unloaded.clear();
    }

    @SubscribeEvent
    public static void onLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        UUID uuid = event.getPlayer().getUUID();
        if (uuid != null && unloaded.containsKey(uuid)) {
            Location location = unloaded.get(uuid);
            MinecraftServer server = event.getPlayer().getServer();
            if (location != null && server != null) {
                ServerWorld level = server.getLevel(location.level);
                if (level != null) {
                    loadChunk(uuid, level, location.blockPos);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getPlayer().getUUID();
        if (uuid != null && loaded.containsKey(uuid)) {
            Location location = loaded.get(uuid);
            MinecraftServer server = event.getPlayer().getServer();
            if (location != null && server != null) {
                ServerWorld level = server.getLevel(location.level);
                if (level != null) {
                    unloadChunk(uuid, level, location.blockPos);
                }
            }
        }
    }

    @SuppressWarnings("java:S3824") // containsKey cannot be replaced with computeIfAbsent
    public static void loadChunk(UUID owner, ServerWorld level, BlockPos pos) {
        if (Boolean.TRUE.equals(ConfigManager.SERVER.infusionChunkloads.get()) && !loaded.containsKey(owner)) {
            Location location = Location.of(level, pos);
            unloaded.remove(owner, location);
            loaded.put(owner, location);
            ForgeChunkManager.forceChunk(level, Pylons.MODID, pos, location.chunkX, location.chunkZ, true, false);
        }
    }

    public static void unloadChunk(UUID owner, ServerWorld level, BlockPos pos) {
        Location location = Location.of(level, pos);
        loaded.remove(owner, location);
        unloaded.put(owner, location);
        ForgeChunkManager.forceChunk(level, Pylons.MODID, pos, location.chunkX, location.chunkZ, false, false);
    }

    @SuppressWarnings("java:S1172") // unused parameter is required
    public static void validateTickets(ServerWorld level, ForgeChunkManager.TicketHelper ticketHelper) {
        ticketHelper.getBlockTickets().keySet().forEach(ticketHelper::removeAllTickets);
    }

    private static class Location {
        protected final RegistryKey<World> level;
        protected final BlockPos blockPos;
        protected final int chunkX;
        protected final int chunkZ;

        public Location(RegistryKey<World> level, BlockPos blockPos, int chunkX, int chunkZ) {
            this.level = level;
            this.blockPos = blockPos;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        static Location of(ServerWorld level, BlockPos blockPos) {
            int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
            int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());
            return new Location(level.dimension(), blockPos, chunkX, chunkZ);
        }
    }
}
