package net.permutated.pylons;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.util.Unit;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.pylons.block.ExpulsionPylonBlock;
import net.permutated.pylons.block.HarvesterPylonBlock;
import net.permutated.pylons.block.InfusionPylonBlock;
import net.permutated.pylons.block.InterdictionPylonBlock;
import net.permutated.pylons.inventory.container.ExpulsionPylonContainer;
import net.permutated.pylons.inventory.container.HarvesterPylonContainer;
import net.permutated.pylons.inventory.container.InfusionPylonContainer;
import net.permutated.pylons.inventory.container.InterdictionPylonContainer;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.item.PotionFilterCard;
import net.permutated.pylons.tile.AbstractPylonTile;
import net.permutated.pylons.tile.ExpulsionPylonTile;
import net.permutated.pylons.tile.HarvesterPylonTile;
import net.permutated.pylons.tile.InfusionPylonTile;
import net.permutated.pylons.tile.InterdictionPylonTile;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

public class ModRegistry {
    private ModRegistry() {
        // nothing to do
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Pylons.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Pylons.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Pylons.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Pylons.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Pylons.MODID);


    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable(TranslationKey.tab()))
        .icon(() -> ModRegistry.PLAYER_FILTER.get().getDefaultInstance())
        .displayItems((parameters, output) -> ITEMS.getEntries().stream()
            .map(RegistryObject::get)
            .map(Item::getDefaultInstance)
            .forEach(output::accept))
        .build()
    );

    // Items
    public static final RegistryObject<Item> PLAYER_FILTER = ITEMS.register("player_filter", PlayerFilterCard::new);
    public static final RegistryObject<Item> POTION_FILTER = ITEMS.register("potion_filter", PotionFilterCard::new);
    public static final RegistryObject<Item> MOB_FILTER = ITEMS.register("mob_filter", MobFilterCard::new);

    // Blocks
    public static final RegistryObject<Block> EXPULSION_PYLON = BLOCKS.register(Constants.EXPULSION_PYLON, ExpulsionPylonBlock::new);
    public static final RegistryObject<Block> INFUSION_PYLON = BLOCKS.register(Constants.INFUSION_PYLON, InfusionPylonBlock::new);
    public static final RegistryObject<Block> HARVESTER_PYLON = BLOCKS.register(Constants.HARVESTER_PYLON, HarvesterPylonBlock::new);
    public static final RegistryObject<Block> INTERDICTION_PYLON = BLOCKS.register(Constants.INTERDICTION_PYLON, InterdictionPylonBlock::new);

    // BlockItems
    public static final RegistryObject<BlockItem> EXPULSION_PYLON_ITEM = blockItem(EXPULSION_PYLON);
    public static final RegistryObject<BlockItem> INFUSION_PYLON_ITEM = blockItem(INFUSION_PYLON);
    public static final RegistryObject<BlockItem> HARVESTER_PYLON_ITEM = blockItem(HARVESTER_PYLON);
    public static final RegistryObject<BlockItem> INTERDICTION_PYLON_ITEM = blockItem(INTERDICTION_PYLON);

    // Tiles
    public static final RegistryObject<BlockEntityType<ExpulsionPylonTile>> EXPULSION_PYLON_TILE = blockEntity(EXPULSION_PYLON, ExpulsionPylonTile::new);
    public static final RegistryObject<BlockEntityType<InfusionPylonTile>> INFUSION_PYLON_TILE = blockEntity(INFUSION_PYLON, InfusionPylonTile::new);
    public static final RegistryObject<BlockEntityType<HarvesterPylonTile>> HARVESTER_PYLON_TILE = blockEntity(HARVESTER_PYLON, HarvesterPylonTile::new);
    public static final RegistryObject<BlockEntityType<InterdictionPylonTile>> INTERDICTION_PYLON_TILE = blockEntity(INTERDICTION_PYLON, InterdictionPylonTile::new);

    // Containers
    public static final RegistryObject<MenuType<ExpulsionPylonContainer>> EXPULSION_PYLON_CONTAINER = container(Constants.EXPULSION_PYLON, ExpulsionPylonContainer::new);
    public static final RegistryObject<MenuType<InfusionPylonContainer>> INFUSION_PYLON_CONTAINER = container(Constants.INFUSION_PYLON, InfusionPylonContainer::new);
    public static final RegistryObject<MenuType<HarvesterPylonContainer>> HARVESTER_PYLON_CONTAINER = container(Constants.HARVESTER_PYLON, HarvesterPylonContainer::new);
    public static final RegistryObject<MenuType<InterdictionPylonContainer>> INTERDICTION_PYLON_CONTAINER = container(Constants.INTERDICTION_PYLON, InterdictionPylonContainer::new);

    /**
     * Register a BlockItem for a Block
     *
     * @param registryObject the Block
     * @return the new registry object
     */
    private static RegistryObject<BlockItem> blockItem(RegistryObject<Block> registryObject) {
        return ITEMS.register(registryObject.getId().getPath(),
            () -> new BlockItem(registryObject.get(), new Item.Properties()));
    }

    /**
     * Register simple materials with no extra properties
     *
     * @param path registry location
     * @return the new registry object
     */
    private static RegistryObject<Item> material(String path) {
        return ITEMS.register(path, () -> new Item(new Item.Properties()));
    }

    /**
     * Used as a NOOP type for the tile registry builder to avoid passing null
     *
     * @see BlockEntityType.Builder#build(Type)
     * @see #blockEntity(RegistryObject, BlockEntitySupplier)
     */
    private static final Type<Unit> EMPTY_PART = new EmptyPart();

    /**
     * Register a tile entity for a Block
     *
     * @param registryObject a registry object containing a Block
     * @param supplier       a Supplier that returns the new Block Entity
     * @return the new registry object
     */
    private static <T extends AbstractPylonTile> RegistryObject<BlockEntityType<T>> blockEntity(RegistryObject<Block> registryObject, BlockEntitySupplier<T> supplier) {
        return TILES.register(registryObject.getId().getPath(),
            () -> BlockEntityType.Builder.of(supplier, registryObject.get()).build(EMPTY_PART));
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> container(String path, IContainerFactory<T> supplier) {
        return CONTAINERS.register(path, () -> IForgeMenuType.create(supplier));
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        CREATIVE_TABS.register(bus);
    }
}
