package net.permutated.pylons.util;

public class Constants {
    private Constants() {
        // nothing to do
    }

    public static final String EXPULSION_PYLON = "expulsion_pylon";
    public static final String INFUSION_PYLON = "infusion_pylon";


    public static class NBT {
        private NBT() {
            // nothing to do
        }

        public static final String ENERGY = "energy";
        public static final String OWNER = "owner";
        public static final String NAME = "name";
        public static final String UUID = "uuid";
        public static final String INV = "inv";

        public static final String EFFECT = "effect";
        public static final String DURATION = "duration";
        public static final String AMPLIFIER = "amplifier";
    }

    public static class JSON {
        private JSON() {
            // nothing to do
        }

        public static final String INPUT = "input";
        public static final String OUTPUT = "output";
        public static final String ITEM = "item";
        public static final String COUNT = "count";
        public static final String NBT = "nbt";
        public static final String TAG = "tag";
    }
}
