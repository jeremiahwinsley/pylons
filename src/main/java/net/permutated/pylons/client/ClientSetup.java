package net.permutated.pylons.client;

import net.minecraft.client.gui.ScreenManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.client.gui.ExpulsionPylonScreen;
import net.permutated.pylons.client.gui.HarvesterPylonScreen;
import net.permutated.pylons.client.gui.InfusionPylonScreen;
import net.permutated.pylons.client.gui.InterdictionPylonScreen;

public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    public static void register() {
        ScreenManager.register(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), ExpulsionPylonScreen::new);
        ScreenManager.register(ModRegistry.INFUSION_PYLON_CONTAINER.get(), InfusionPylonScreen::new);
        ScreenManager.register(ModRegistry.HARVESTER_PYLON_CONTAINER.get(), HarvesterPylonScreen::new);
        ScreenManager.register(ModRegistry.INTERDICTION_PYLON_CONTAINER.get(), InterdictionPylonScreen::new);
    }
}
