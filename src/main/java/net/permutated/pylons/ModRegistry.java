package net.permutated.pylons;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.util.Unit;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.permutated.pylons.components.EntityComponent;
import net.permutated.pylons.components.PlayerComponent;
import net.permutated.pylons.components.PotionComponent;
import net.permutated.pylons.item.LifelessFilterCard;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.item.PotionFilterCard;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonBlock;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonContainer;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonTile;
import net.permutated.pylons.machines.harvester.HarvesterPylonBlock;
import net.permutated.pylons.machines.harvester.HarvesterPylonContainer;
import net.permutated.pylons.machines.harvester.HarvesterPylonTile;
import net.permutated.pylons.machines.infusion.InfusionPylonBlock;
import net.permutated.pylons.machines.infusion.InfusionPylonContainer;
import net.permutated.pylons.machines.infusion.InfusionPylonTile;
import net.permutated.pylons.machines.interdiction.InterdictionPylonBlock;
import net.permutated.pylons.machines.interdiction.InterdictionPylonContainer;
import net.permutated.pylons.machines.interdiction.InterdictionPylonTile;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import java.util.function.Supplier;

public class ModRegistry {
    private ModRegistry() {
        // nothing to do
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Pylons.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Pylons.MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Pylons.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Pylons.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Pylons.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Pylons.MODID);

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable(TranslationKey.tab()))
        .icon(() -> ModRegistry.PLAYER_FILTER.get().getDefaultInstance())
        .displayItems((parameters, output) -> ITEMS.getEntries().stream()
            .map(Supplier::get)
            .map(Item::getDefaultInstance)
            .forEach(output::accept))
        .build()
    );

    // Items
    public static final Supplier<Item> PLAYER_FILTER = ITEMS.register("player_filter", PlayerFilterCard::new);
    public static final Supplier<Item> POTION_FILTER = ITEMS.register("potion_filter", PotionFilterCard::new);
    public static final Supplier<Item> MOB_FILTER = ITEMS.register("mob_filter", MobFilterCard::new);
    public static final Supplier<Item> LIFELESS_FILTER = ITEMS.register("lifeless_filter", LifelessFilterCard::new);

    // Components
    public static final Supplier<DataComponentType<PlayerComponent>> PLAYER_COMPONENT = COMPONENTS.registerComponentType(
        "player", builder -> builder.persistent(PlayerComponent.BASIC_CODEC)
    );

    public static final Supplier<DataComponentType<PotionComponent>> POTION_COMPONENT = COMPONENTS.registerComponentType(
        "potion", builder -> builder.persistent(PotionComponent.BASIC_CODEC)
    );

    public static final Supplier<DataComponentType<EntityComponent>> ENTITY_COMPONENT = COMPONENTS.registerComponentType(
        "entity", builder -> builder.persistent(EntityComponent.BASIC_CODEC)
    );

    // Blocks
    public static final DeferredBlock<Block> EXPULSION_PYLON = BLOCKS.register(Constants.EXPULSION_PYLON, ExpulsionPylonBlock::new);
    public static final DeferredBlock<Block> INFUSION_PYLON = BLOCKS.register(Constants.INFUSION_PYLON, InfusionPylonBlock::new);
    public static final DeferredBlock<Block> HARVESTER_PYLON = BLOCKS.register(Constants.HARVESTER_PYLON, HarvesterPylonBlock::new);
    public static final DeferredBlock<Block> INTERDICTION_PYLON = BLOCKS.register(Constants.INTERDICTION_PYLON, InterdictionPylonBlock::new);

    // BlockItems
    public static final Supplier<BlockItem> EXPULSION_PYLON_ITEM = blockItem(EXPULSION_PYLON);
    public static final Supplier<BlockItem> INFUSION_PYLON_ITEM = blockItem(INFUSION_PYLON);
    public static final Supplier<BlockItem> HARVESTER_PYLON_ITEM = blockItem(HARVESTER_PYLON);
    public static final Supplier<BlockItem> INTERDICTION_PYLON_ITEM = blockItem(INTERDICTION_PYLON);

    // Tiles
    public static final Supplier<BlockEntityType<ExpulsionPylonTile>> EXPULSION_PYLON_TILE = blockEntity(EXPULSION_PYLON, ExpulsionPylonTile::new);
    public static final Supplier<BlockEntityType<InfusionPylonTile>> INFUSION_PYLON_TILE = blockEntity(INFUSION_PYLON, InfusionPylonTile::new);
    public static final Supplier<BlockEntityType<HarvesterPylonTile>> HARVESTER_PYLON_TILE = blockEntity(HARVESTER_PYLON, HarvesterPylonTile::new);
    public static final Supplier<BlockEntityType<InterdictionPylonTile>> INTERDICTION_PYLON_TILE = blockEntity(INTERDICTION_PYLON, InterdictionPylonTile::new);

    // Containers
    public static final Supplier<MenuType<ExpulsionPylonContainer>> EXPULSION_PYLON_CONTAINER = container(Constants.EXPULSION_PYLON, ExpulsionPylonContainer::new);
    public static final Supplier<MenuType<InfusionPylonContainer>> INFUSION_PYLON_CONTAINER = container(Constants.INFUSION_PYLON, InfusionPylonContainer::new);
    public static final Supplier<MenuType<HarvesterPylonContainer>> HARVESTER_PYLON_CONTAINER = container(Constants.HARVESTER_PYLON, HarvesterPylonContainer::new);
    public static final Supplier<MenuType<InterdictionPylonContainer>> INTERDICTION_PYLON_CONTAINER = container(Constants.INTERDICTION_PYLON, InterdictionPylonContainer::new);

    /**
     * Register a BlockItem for a Block
     *
     * @param holder the Block
     * @return the new registry object
     */
    private static DeferredItem<BlockItem> blockItem(Holder<Block> holder) {
        return ITEMS.register(holder.unwrapKey().map(ResourceKey::location).map(ResourceLocation::getPath).orElseThrow(),
            () -> new BlockItem(holder.value(), new Item.Properties()));
    }

    /**
     * Register simple materials with no extra properties
     *
     * @param path registry location
     * @return the new registry object
     */
    private static DeferredItem<Item> material(String path) {
        return ITEMS.register(path, () -> new Item(new Item.Properties()));
    }

    /**
     * Used as a NOOP type for the tile registry builder to avoid passing null
     *
     * @see BlockEntityType.Builder#build(Type)
     * @see #blockEntity(Holder, BlockEntityType.BlockEntitySupplier)
     */
    private static final Type<Unit> EMPTY_PART = new EmptyPart();

    /**
     * Register a tile entity for a Block
     *
     * @param holder   a Holder containing a Block
     * @param supplier a Supplier that returns the new Block Entity
     * @return the new registry object
     */
    private static <T extends AbstractPylonTile> Supplier<BlockEntityType<T>> blockEntity(Holder<Block> holder, BlockEntityType.BlockEntitySupplier<T> supplier) {
        return TILES.register(holder.unwrapKey().map(ResourceKey::location).map(ResourceLocation::getPath).orElseThrow(),
            () -> BlockEntityType.Builder.of(supplier, holder.value()).build(EMPTY_PART));
    }

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> container(String path, IContainerFactory<T> supplier) {
        return CONTAINERS.register(path, () -> IMenuTypeExtension.create(supplier));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        BLOCKS.register(bus);
        COMPONENTS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        CREATIVE_TABS.register(bus);
    }
}
