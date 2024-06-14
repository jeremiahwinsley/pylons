package net.permutated.pylons.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.ResourceUtil;

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

    protected void pylon(DeferredBlock<Block> block, String texture) {
        ResourceLocation key = block.getKey().location();
        String blockName = Objects.requireNonNull(key).toString();
        ModelFile pylonModel = models()
            .withExistingParent(blockName, ResourceUtil.prefix("block/pylon"))
            .texture("particle", ResourceLocation.withDefaultNamespace("block/".concat(texture)))
            .texture("center", ResourceLocation.withDefaultNamespace("block/".concat(texture)));
        simpleBlock(block.get(), pylonModel);
        simpleBlockItem(block.get(), pylonModel);
    }
}
