package net.permutated.pylons.util;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Location {
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


    public static Location of(ServerWorld level, BlockPos blockPos) {
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
    public static Set<Location> chunkSet(ServerWorld level, BlockPos blockPos, Range range) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return chunkX == location.chunkX
            && chunkZ == location.chunkZ
            && level.equals(location.level)
            && blockPos.equals(location.blockPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, blockPos, chunkX, chunkZ);
    }

    @Override
    public String toString() {
        return "Location{" +
            "level=" + level +
            ", blockPos=" + blockPos +
            ", chunkX=" + chunkX +
            ", chunkZ=" + chunkZ +
            '}';
    }
}
