package net.permutated.pylons;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonScreen;
import net.permutated.pylons.machines.harvester.HarvesterPylonScreen;
import net.permutated.pylons.machines.infusion.InfusionPylonScreen;
import net.permutated.pylons.machines.interdiction.InterdictionPylonScreen;
import net.permutated.pylons.machines.protection.ProtectionPylonScreen;

@EventBusSubscriber(modid = Pylons.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    @SubscribeEvent
    public static void registerMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), ExpulsionPylonScreen::new);
        event.register(ModRegistry.INFUSION_PYLON_CONTAINER.get(), InfusionPylonScreen::new);
        event.register(ModRegistry.HARVESTER_PYLON_CONTAINER.get(), HarvesterPylonScreen::new);
        event.register(ModRegistry.INTERDICTION_PYLON_CONTAINER.get(), InterdictionPylonScreen::new);
        event.register(ModRegistry.PROTECTION_PYLON_CONTAINER.get(), ProtectionPylonScreen::new);
    }
}
