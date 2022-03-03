package net.permutated.pylons.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.permutated.pylons.util.ChunkManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.item.PotionFilterCard;

import java.util.ArrayList;
import java.util.List;

public class InfusionPylonTile extends AbstractPylonTile {
    public InfusionPylonTile() {
        super(ModRegistry.INFUSION_PYLON_TILE.get());
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof PotionFilterCard;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide && canTick(60) && owner != null) {
            MinecraftServer server = level.getServer();

            if (server != null) {
                ChunkManager.loadChunk(owner, (ServerWorld) level, getBlockPos());
                PlayerEntity player = server.getPlayerList().getPlayer(owner);

                if (player != null && player.isAffectedByPotions()) {
                    for (EffectInstance effect : getEffects()) {
                        player.addEffect(effect);
                    }
                }
            }
        }
    }

    public List<EffectInstance> getEffects() {
        List<EffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack stack = itemStackHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof PotionFilterCard && PotionFilterCard.isAllowed(stack)) {
                Effect effect = PotionFilterCard.getEffect(stack);
                int duration = PotionFilterCard.getDuration(stack);
                int amplifier = PotionFilterCard.getAmplifier(stack);

                if (duration >= PotionFilterCard.getRequiredDuration() && effect != null) {
                    // defaults to 400 ticks / 20 seconds of effect
                    effects.add(new EffectInstance(effect, PotionFilterCard.getAppliedDuration(), amplifier, false, false));
                }
            }
        }
        return effects;
    }
}
