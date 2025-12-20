package net.permutated.pylons.compat.harvest.adapters;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.permutated.pylons.compat.harvest.Harvestable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgricraftCrop implements Harvestable {
    @Override
    public Block getBlock() {
        return ModBlocks.CROP.get();
    }

    @Override
    public boolean isHarvestable(Level level, BlockPos pos, BlockState blockState) {
        Optional<AgriCrop> optional = AgriApi.getCrop(level, pos);
        return optional.map(AgriCrop::canBeHarvested).orElse(false);
    }

    @Override
    public List<ItemStack> harvest(Level level, BlockPos pos, BlockState blockState) {
        Optional<AgriCrop> optional = AgriApi.getCrop(level, pos);

        List<ItemStack> items = new ArrayList<>();
        optional.ifPresent(crop -> crop.harvest(items::add, null));
        return items;
    }
}
