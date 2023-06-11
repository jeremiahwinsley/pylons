package net.permutated.pylons;

import net.minecraft.client.gui.screens.MenuScreens;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonScreen;
import net.permutated.pylons.machines.harvester.HarvesterPylonScreen;
import net.permutated.pylons.machines.infusion.InfusionPylonScreen;
import net.permutated.pylons.machines.interdiction.InterdictionPylonScreen;

public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    public static void register() {
        MenuScreens.register(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), ExpulsionPylonScreen::new);
        MenuScreens.register(ModRegistry.INFUSION_PYLON_CONTAINER.get(), InfusionPylonScreen::new);
        MenuScreens.register(ModRegistry.HARVESTER_PYLON_CONTAINER.get(), HarvesterPylonScreen::new);
        MenuScreens.register(ModRegistry.INTERDICTION_PYLON_CONTAINER.get(), InterdictionPylonScreen::new);
    }
}
