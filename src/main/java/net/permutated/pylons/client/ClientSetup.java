package net.permutated.pylons.client;

import net.minecraft.client.gui.ScreenManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.client.gui.ExpulsionPylonScreen;

public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    public static void register() {
        ScreenManager.register(ModRegistry.EXPULSION_PYLON_CONTAINER.get(), ExpulsionPylonScreen::new);
    }
}
