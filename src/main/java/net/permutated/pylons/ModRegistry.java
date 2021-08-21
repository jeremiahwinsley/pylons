package net.permutated.pylons;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.util.Unit;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.permutated.pylons.block.ExpulsionPylonBlock;
import net.permutated.pylons.block.InfusionPylonBlock;
import net.permutated.pylons.inventory.container.ExpulsionPylonContainer;
import net.permutated.pylons.inventory.container.InfusionPylonContainer;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.item.PotionFilterCard;
import net.permutated.pylons.tile.AbstractPylonTile;
import net.permutated.pylons.tile.ExpulsionPylonTile;
import net.permutated.pylons.tile.InfusionPylonTile;
import net.permutated.pylons.util.Constants;

import java.util.function.Supplier;

public class ModRegistry {
    private ModRegistry() {
        // nothing to do
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Pylons.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Pylons.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Pylons.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Pylons.MODID);


    public static final ItemGroup CREATIVE_TAB = new ModItemGroup(Pylons.MODID,
        () -> new ItemStack(ModRegistry.PLAYER_FILTER.get()));

    // Items
    public static final RegistryObject<Item> PLAYER_FILTER = ITEMS.register("player_filter", PlayerFilterCard::new);
    public static final RegistryObject<Item> POTION_FILTER = ITEMS.register("potion_filter", PotionFilterCard::new);

    // Blocks
    public static final RegistryObject<Block> EXPULSION_PYLON = BLOCKS.register(Constants.EXPULSION_PYLON, ExpulsionPylonBlock::new);
    public static final RegistryObject<Block> INFUSION_PYLON = BLOCKS.register(Constants.INFUSION_PYLON, InfusionPylonBlock::new);

    // BlockItems
    public static final RegistryObject<BlockItem> EXPULSION_PYLON_ITEM = blockItem(EXPULSION_PYLON);
    public static final RegistryObject<BlockItem> INFUSION_PYLON_ITEM = blockItem(INFUSION_PYLON);

    // Tiles
    public static final RegistryObject<TileEntityType<ExpulsionPylonTile>> EXPULSION_PYLON_TILE = tile(EXPULSION_PYLON, ExpulsionPylonTile::new);
    public static final RegistryObject<TileEntityType<InfusionPylonTile>> INFUSION_PYLON_TILE = tile(INFUSION_PYLON, InfusionPylonTile::new);

    // Containers
    public static final RegistryObject<ContainerType<ExpulsionPylonContainer>> EXPULSION_PYLON_CONTAINER = container(Constants.EXPULSION_PYLON, ExpulsionPylonContainer::new);
    public static final RegistryObject<ContainerType<InfusionPylonContainer>> INFUSION_PYLON_CONTAINER = container(Constants.INFUSION_PYLON, InfusionPylonContainer::new);

    /**
     * Register a BlockItem for a Block
     *
     * @param registryObject the Block
     * @return the new registry object
     */
    private static RegistryObject<BlockItem> blockItem(RegistryObject<Block> registryObject) {
        return ITEMS.register(registryObject.getId().getPath(),
            () -> new BlockItem(registryObject.get(), new Item.Properties().tab(CREATIVE_TAB)));
    }

    /**
     * Register simple materials with no extra properties
     *
     * @param path registry location
     * @return the new registry object
     */
    private static RegistryObject<Item> material(String path) {
        return ITEMS.register(path, () -> new Item(new Item.Properties().tab(CREATIVE_TAB)));
    }

    /**
     * Used as a NOOP type for the tile registry builder to avoid passing null
     *
     * @see TileEntityType.Builder#build(Type)
     * @see #tile(RegistryObject, Supplier)
     */
    private static final Type<Unit> EMPTY_PART = new EmptyPart();

    /**
     * Register a tile entity for a Block
     *
     * @param registryObject a registry object containing a Block
     * @param supplier       a Supplier that returns the new Tile Entity
     * @return the new registry object
     */
    private static <T extends AbstractPylonTile> RegistryObject<TileEntityType<T>> tile(RegistryObject<Block> registryObject, Supplier<T> supplier) {
        return TILES.register(registryObject.getId().getPath(),
            () -> TileEntityType.Builder.of(supplier, registryObject.get()).build(EMPTY_PART));
    }

    private static <T extends Container> RegistryObject<ContainerType<T>> container(String path, IContainerFactory<T> supplier) {
        return CONTAINERS.register(path, () -> IForgeContainerType.create(supplier));
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
    }

    public static final class ModItemGroup extends ItemGroup {
        private final Supplier<ItemStack> iconSupplier;

        public ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Override
        public ItemStack makeIcon() {
            return iconSupplier.get();
        }
    }
}
