package net.permutated.pylons.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.client.gui.ExpulsionPylonScreen;
import net.permutated.pylons.client.gui.HarvesterPylonScreen;
import net.permutated.pylons.client.gui.InfusionPylonScreen;

public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    public static void register() {
        MenuScreens.register(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), ExpulsionPylonScreen::new);
        MenuScreens.register(ModRegistry.INFUSION_PYLON_CONTAINER.get(), InfusionPylonScreen::new);
        MenuScreens.register(ModRegistry.HARVESTER_PYLON_CONTAINER.get(), HarvesterPylonScreen::new);
    }
}
