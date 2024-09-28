package net.permutated.pylons.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

@EventBusSubscriber(modid = Pylons.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEventHandler {
    private ClientEventHandler() {
        // nothing to do
    }

    @SubscribeEvent
    public static void onClientLoggedOutEvent(final ClientPlayerNetworkEvent.LoggingOut event) {
        Pylons.LOGGER.debug("Clearing recipe cache after logging out");
        ModRegistry.HARVESTING_REGISTRY.clearRecipes();
    }
}
