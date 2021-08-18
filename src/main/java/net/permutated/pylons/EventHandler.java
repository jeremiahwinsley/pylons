package net.permutated.pylons;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.permutated.pylons.item.PlayerFilterCard;

public class EventHandler {
    private EventHandler() {
        // nothing to do
    }

    /**
     * Event subscriber for handling right clicks
     *
     * @param event the PlayerInteractEvent
     */
    public static void onPlayerInteractEvent(final PlayerInteractEvent.EntityInteract event) {
        if (event.getItemStack().getItem() instanceof PlayerFilterCard && event.getTarget() instanceof PlayerEntity) {
            if (event.getSide() == LogicalSide.SERVER) {
                ItemStack itemStack = event.getItemStack();

                CompoundNBT tag = itemStack.getOrCreateTag();
                tag.putUUID("uuid", event.getTarget().getUUID());

                event.setCancellationResult(ActionResultType.SUCCESS);
            } else {
                event.setCancellationResult(ActionResultType.CONSUME);
            }
            event.setCanceled(true);
        }
    }
}
