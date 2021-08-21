package net.permutated.pylons;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.permutated.pylons.client.ClientSetup;
import net.permutated.pylons.item.PlayerFilterCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Pylons.MODID)
public class Pylons
{
    public static final String MODID = "pylons";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Pylons() {
        LOGGER.info("Registering mod: {}", MODID);

        ModRegistry.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetupEvent);
        MinecraftForge.EVENT_BUS.addListener(PlayerFilterCard::onPlayerInteractEvent);
    }

    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        ClientSetup.register();
    }
}
