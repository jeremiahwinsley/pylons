package net.permutated.pylons.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.item.PotionFilterCard;

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
            if (!stack.isEmpty() && stack.getItem() instanceof PotionFilterCard) {
                MobEffect effect = PotionFilterCard.getEffect(stack);
                int duration = PotionFilterCard.getDuration(stack);
                int amplifier = PotionFilterCard.getAmplifier(stack);

                if (duration >= PotionFilterCard.REQUIRED && effect != null) {
                    // 300 ticks - 15 seconds
                    effects.add(new MobEffectInstance(effect, 300, amplifier, false, false));
                }
            }
        }
        return effects;
    }
}
