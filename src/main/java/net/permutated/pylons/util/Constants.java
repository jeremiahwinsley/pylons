package net.permutated.pylons.util;

public class Constants {
    private Constants() {
        // nothing to do
    }

    public static final String EXPULSION_PYLON = "expulsion_pylon";
    public static final String BEACON_PYLON = "beacon_pylon";


    public static class NBT {
        private NBT() {
            // nothing to do
        }

        public static final String ENERGY = "energy";
        public static final String OWNER = "owner";
        public static final String NAME = "name";
        public static final String UUID = "uuid";
        public static final String INV = "inv";
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
