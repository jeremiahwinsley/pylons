package net.permutated.pylons.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.permutated.pylons.Pylons;


public class NetworkDispatcher {
    private NetworkDispatcher() {
        // nothing to do
    }

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Pylons.MODID, "main"),
        () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        int packetIndex = 0;
        INSTANCE.registerMessage(packetIndex++, PacketButtonClicked.class, PacketButtonClicked::toBytes, PacketButtonClicked::new, PacketButtonClicked::handle);

        Pylons.LOGGER.info("Registered {} network packets", packetIndex);
    }
}
