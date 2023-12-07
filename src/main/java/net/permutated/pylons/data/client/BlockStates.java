package net.permutated.pylons.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

import java.util.Objects;

public class BlockStates extends BlockStateProvider {
    public BlockStates(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, Pylons.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        pylon(ModRegistry.EXPULSION_PYLON, "diamond_block");
        pylon(ModRegistry.INFUSION_PYLON, "emerald_block");
        pylon(ModRegistry.HARVESTER_PYLON, "hay_block_side");
        pylon(ModRegistry.INTERDICTION_PYLON, "netherite_block");
    }

    protected void pylon(RegistryObject<Block> block, String texture) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block.get());
        String blockName = Objects.requireNonNull(key).toString();
        ModelFile pylonModel = models()
            .withExistingParent(blockName, new ResourceLocation(Pylons.MODID, "block/pylon"))
            .texture("particle", new ResourceLocation("block/".concat(texture)))
            .texture("center", new ResourceLocation("block/".concat(texture)));
        simpleBlock(block.get(), pylonModel);
        simpleBlockItem(block.get(), pylonModel);
    }
}
