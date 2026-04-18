package net.permutated.pylons;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.permutated.pylons.util.PylonBlockTintSource;

import java.util.List;

@Mod(value = Pylons.MODID, dist = Dist.CLIENT)
public class PylonsClient {
    public PylonsClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::onRegisterColorHandlersEvent);
    }

    public void onRegisterColorHandlersEvent(final RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(new PylonBlockTintSource()),
            ModRegistry.EXPULSION_PYLON.get(),
            ModRegistry.INFUSION_PYLON.get(),
            ModRegistry.HARVESTER_PYLON.get(),
            ModRegistry.INTERDICTION_PYLON.get(),
            ModRegistry.PROTECTION_PYLON.get()
        );
    }
}
