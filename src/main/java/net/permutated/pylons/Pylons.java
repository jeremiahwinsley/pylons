package net.permutated.pylons;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.permutated.pylons.machines.base.AbstractPylonBlock;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.network.NetworkDispatcher;
import net.permutated.pylons.util.ChunkManager;
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
        NetworkDispatcher.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigManager.SERVER_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetupEvent);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetupEvent);
        MinecraftForge.EVENT_BUS.addListener(PlayerFilterCard::onPlayerInteractEvent);
        MinecraftForge.EVENT_BUS.addListener(MobFilterCard::onPlayerInteractEvent);
        MinecraftForge.EVENT_BUS.addListener(Pylons::onBlockBreakEvent);
    }

    public void onCommonSetupEvent(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ForgeChunkManager.setForcedChunkLoadingCallback(MODID, ChunkManager::validateTickets));
    }

    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        ClientSetup.register();
    }

    public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof AbstractPylonBlock) {
            BlockEntity tileEntity = event.getLevel().getBlockEntity(event.getPos());

            if (!AbstractPylonBlock.canAccessPylon(tileEntity, event.getPlayer())) {
                event.setCanceled(true);
            }
        }
    }
}
