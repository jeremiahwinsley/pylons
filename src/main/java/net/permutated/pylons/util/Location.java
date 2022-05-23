package net.permutated.pylons.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;

public record Location(ResourceKey<net.minecraft.world.level.Level> level, BlockPos blockPos, int chunkX, int chunkZ) {

    public static Location of(ServerLevel level, BlockPos blockPos) {
        int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());
        return new Location(level.dimension(), blockPos, chunkX, chunkZ);
    }

    /**
     * Converts a given Level and BlockPos to a set of Locations containing each chunk within the given Range.
     * The resulting Location has the BlockPos set to BlockPos.ZERO so that Location#equals can be used
     * as HashMap keys without knowing the original block location.
     * @param level the level where the pylon is placed
     * @param blockPos the position of the pylon
     * @param range the current range setting of the pylon
     * @return a set of Locations
     */
    public static Set<Location> chunkSet(ServerLevel level, BlockPos blockPos, Range range) {
        Set<Location> locations = new HashSet<>();

        int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());

        int radius = range.toRadius();

        int minChunkX = chunkX - radius;
        int minChunkZ = chunkZ - radius;
        int maxChunkX = chunkX + radius;
        int maxChunkZ = chunkZ + radius;

        for (int x = minChunkX;x <= maxChunkX;x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                locations.add(new Location(level.dimension(), BlockPos.ZERO, x, z));
            }
        }

        return locations;
    }
}
