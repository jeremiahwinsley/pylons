package net.permutated.pylons.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.ResourceUtil;

import java.util.Collection;

public class ItemModels extends ItemModelProvider {
    public ItemModels(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, Pylons.MODID, fileHelper);
    }

    private ResourceLocation res(String name) {
        return ResourceUtil.prefix("item/".concat(name));
    }

    @Override
    protected void registerModels() {
        Collection<DeferredHolder<Item, ? extends Item>> entries = ModRegistry.ITEMS.getEntries();

        ResourceLocation generated = ResourceLocation.withDefaultNamespace("item/generated");

        entries.stream()
            .filter(item -> !(item.get() instanceof BlockItem))
            .forEach(item -> {
                String name = item.getId().getPath();
                withExistingParent(name, generated)
                    .texture("layer0", res(name));
            });

    }


}
