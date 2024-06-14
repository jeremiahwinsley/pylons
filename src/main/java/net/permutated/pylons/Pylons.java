package net.permutated.pylons;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.permutated.pylons.compat.harvest.HarvestCompat;
import net.permutated.pylons.compat.teams.TeamCompat;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.machines.base.AbstractPylonBlock;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.network.PacketButtonClicked;
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

    public Pylons(IEventBus modEventBus) {
        LOGGER.info("Registering mod: {}", MODID);

        ModRegistry.register(modEventBus);
        TeamCompat.init();

        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, ConfigManager.SERVER_SPEC);
        modEventBus.addListener(this::onCommonSetupEvent);
        modEventBus.addListener(this::onRegisterPayloadHandlersEvent);
        modEventBus.addListener(this::onRegisterCapabilitiesEvent);
        modEventBus.addListener(ChunkManager::onRegisterTicketControllersEvent);

        NeoForge.EVENT_BUS.addListener(PlayerFilterCard::onPlayerInteractEvent);
        NeoForge.EVENT_BUS.addListener(MobFilterCard::onPlayerInteractEvent);
        NeoForge.EVENT_BUS.addListener(Pylons::onBlockBreakEvent);
    }

    public void onCommonSetupEvent(final FMLCommonSetupEvent event) {
        event.enqueueWork(HarvestCompat::init);
    }

    public void onRegisterPayloadHandlersEvent(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Pylons.MODID);
        registrar.playToServer(PacketButtonClicked.TYPE, PacketButtonClicked.STREAM_CODEC, PacketButtonClicked::handle);
    }

    public void onRegisterCapabilitiesEvent(final RegisterCapabilitiesEvent event) {
        AbstractPylonTile.registerCapabilities(event, ModRegistry.HARVESTER_PYLON_TILE.get());
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
