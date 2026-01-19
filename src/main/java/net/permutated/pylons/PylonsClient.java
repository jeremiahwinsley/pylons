package net.permutated.pylons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.permutated.pylons.machines.base.AbstractPylonTile;

import javax.annotation.Nullable;

@Mod(value = Pylons.MODID, dist = Dist.CLIENT)
public class PylonsClient {
    public PylonsClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::onRegisterColorHandlersEvent);
    }

    public void onRegisterColorHandlersEvent(final RegisterColorHandlersEvent.Block event) {
        event.register(this::getBlockColor,
            ModRegistry.EXPULSION_PYLON.get(),
            ModRegistry.INFUSION_PYLON.get(),
            ModRegistry.HARVESTER_PYLON.get(),
            ModRegistry.INTERDICTION_PYLON.get(),
            ModRegistry.PROTECTION_PYLON.get()
        );
    }

    public int getBlockColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level != null && pos != null && level.getBlockEntity(pos) instanceof AbstractPylonTile tile) {
            return tile.getColor();
        }
        return -1;
    }
}
