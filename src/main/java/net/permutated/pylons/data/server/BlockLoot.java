package net.permutated.pylons.data.server;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.pylons.ModRegistry;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockLoot extends LootTableProvider {

    public BlockLoot(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return List.of(Pair.of(Blocks::new, LootContextParamSets.BLOCK));
    }

    private static class Blocks extends net.minecraft.data.loot.BlockLoot
    {
        @Override
        protected void addTables()
        {
            ModRegistry.BLOCKS.getEntries().forEach(block -> dropSelf(block.get()));
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return ModRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
        }
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker)
    {
        map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
    }

}
