package net.permutated.pylons.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.UsernameCache;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PlayerFilterCard extends Item {
    public PlayerFilterCard() {
        super(new Properties().stacksTo(1).tab(ModRegistry.CREATIVE_TAB).setNoRepair());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        return (tag != null && tag.hasUUID(Constants.NBT.UUID));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundNBT tag = stack.getTagElement(Pylons.MODID);
        if (tag != null) {
            UUID uuid = tag.getUUID(Constants.NBT.UUID);
            tooltip.add(new StringTextComponent("UUID: ".concat(uuid.toString())));
        }
    }
}
