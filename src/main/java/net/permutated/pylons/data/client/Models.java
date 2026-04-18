package net.permutated.pylons.data.client;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.ResourceUtil;

import java.util.Collection;
import java.util.Optional;

public class Models extends ModelProvider {
    public static final TextureSlot CENTER = TextureSlot.create("center");
    public static final ModelTemplate PYLON_TEMPLATE = new ModelTemplate(
        Optional.of(ResourceUtil.prefix("block/pylon")),
        Optional.empty(),
        TextureSlot.PARTICLE,
        CENTER
    );

    public Models(PackOutput packOutput) {
        super(packOutput, Pylons.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        pylon(blockModels, ModRegistry.EXPULSION_PYLON, "diamond_block");
        pylon(blockModels, ModRegistry.INFUSION_PYLON, "emerald_block");
        pylon(blockModels, ModRegistry.HARVESTER_PYLON, "hay_block_side");
        pylon(blockModels, ModRegistry.INTERDICTION_PYLON, "netherite_block");
        pylon(blockModels, ModRegistry.PROTECTION_PYLON, "honeycomb_block");

        Collection<DeferredHolder<Item, ? extends Item>> entries = ModRegistry.ITEMS.getEntries();

        entries.stream()
            .filter(item -> !(item.get() instanceof BlockItem))
            .forEach(item -> itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM));
    }

    protected void pylon(BlockModelGenerators blockModels, DeferredBlock<Block> block, String texture) {
        TexturedModel.Provider provider = TexturedModel.createDefault(
            _ -> new TextureMapping()
                .put(TextureSlot.PARTICLE, fromTexture(texture))
                .put(CENTER, fromTexture(texture)),
                PYLON_TEMPLATE
            );

        blockModels.createTrivialBlock(block.get(), provider);
    }

    Material fromTexture(String texture) {
        return new Material(Identifier.withDefaultNamespace("block/".concat(texture)));
    }
}
