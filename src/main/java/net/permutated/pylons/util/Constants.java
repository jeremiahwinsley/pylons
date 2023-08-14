package net.permutated.pylons.util;

public class Constants {
    private Constants() {
        // nothing to do
    }

    public static final String EXPULSION_PYLON = "expulsion_pylon";
    public static final String INFUSION_PYLON = "infusion_pylon";
    public static final String HARVESTER_PYLON = "harvester_pylon";
    public static final String INTERDICTION_PYLON = "interdiction_pylon";

    public static final String UNKNOWN = "unknown";

    public static class NBT {
        private NBT() {
            // nothing to do
        }

        public static final String REGISTRY = "registry";
        public static final String ENERGY = "energy";
        public static final String OWNER = "owner";
        public static final String TEAM = "team";
        public static final String NAME = "name";
        public static final String UUID = "uuid";
        public static final String INV = "inv";

        public static final String EFFECT = "effect";
        public static final String DURATION = "duration";
        public static final String AMPLIFIER = "amplifier";

        public static final String RANGE = "range";
        public static final String ENABLED = "enabled";
        public static final String CONTENTS = "contents";
        public static final String POSITION = "position";
    }
}
