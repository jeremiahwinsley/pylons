package net.permutated.pylons.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.ModRegistry;

import java.util.Collection;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, Pylons.MODID, fileHelper);
    }

    private ResourceLocation res(String name) {
        return new ResourceLocation(Pylons.MODID, "item/".concat(name));
    }

    @Override
    protected void registerModels() {
        Collection<RegistryObject<Item>> entries = ModRegistry.ITEMS.getEntries();

        ResourceLocation generated = new ResourceLocation("item/generated");

        entries.stream()
            .filter(item -> !(item.get() instanceof BlockItem))
            .forEach(item -> {
                String name = item.getId().getPath();
                withExistingParent(name, generated)
                    .texture("layer0", res(name));
            });

    }


}
