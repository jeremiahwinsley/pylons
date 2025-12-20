package net.permutated.pylons.machines.infusion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.ConfigManager;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.components.PotionComponent;
import net.permutated.pylons.item.PotionFilterCard;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.util.ChunkManager;

import java.util.ArrayList;
import java.util.List;

public class InfusionPylonTile extends AbstractPylonTile {
    public InfusionPylonTile(BlockPos pos, BlockState state) {
        super(ModRegistry.INFUSION_PYLON_TILE.get(), pos, state);
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
                Player player = server.getPlayerList().getPlayer(owner);

                if (player != null && player.isAffectedByPotions()) {
                    ChunkManager.loadChunk(owner, (ServerLevel) level, getBlockPos());
                    for (MobEffectInstance effect : getEffects()) {
                        player.addEffect(effect);
                    }
                }
            }
        }
    }

    public List<MobEffectInstance> getEffects() {
        List<MobEffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack stack = itemStackHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof PotionFilterCard && PotionFilterCard.isAllowed(stack) && !PotionFilterCard.isBanned(stack)) {
                PotionComponent data = stack.get(ModRegistry.POTION_COMPONENT);
                if (data != null && data.duration() >= PotionFilterCard.getRequiredDuration()) {
                    // cap amplifier to config value
                    int amplifier = Math.min(data.amplifier(), ConfigManager.SERVER.infusionMaximumPotency.getAsInt());
                    // defaults to 400 ticks / 20 seconds of effect
                    effects.add(new MobEffectInstance(data.effect(), PotionFilterCard.getAppliedDuration(), amplifier, true, false));
                }
            }
        }
        return effects;
    }
}
