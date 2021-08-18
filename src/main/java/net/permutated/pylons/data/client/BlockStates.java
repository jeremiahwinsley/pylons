package net.permutated.pylons.data.client;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

import java.util.Collection;
import java.util.Objects;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, Pylons.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        Collection<RegistryObject<Block>> entries = ModRegistry.BLOCKS.getEntries();

        entries.forEach(block -> {
            String blockName = Objects.requireNonNull(block.get().getRegistryName()).toString();
            ModelFile pylonModel = models().withExistingParent(blockName, new ResourceLocation(Pylons.MODID, "block/pylon"));
            simpleBlock(block.get(), pylonModel);
            simpleBlockItem(block.get(), pylonModel);
        });
    }
}
