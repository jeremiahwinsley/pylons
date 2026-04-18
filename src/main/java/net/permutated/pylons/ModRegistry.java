package net.permutated.pylons;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.permutated.pylons.components.BlockComponent;
import net.permutated.pylons.components.EntityComponent;
import net.permutated.pylons.components.PlayerComponent;
import net.permutated.pylons.components.PotionComponent;
import net.permutated.pylons.item.BlockFilterCard;
import net.permutated.pylons.item.LifelessFilterCard;
import net.permutated.pylons.item.MobFilterCard;
import net.permutated.pylons.item.PlayerFilterCard;
import net.permutated.pylons.item.PotionFilterCard;
import net.permutated.pylons.machines.base.AbstractPylonTile;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonBlock;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonBlockItem;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonContainer;
import net.permutated.pylons.machines.expulsion.ExpulsionPylonTile;
import net.permutated.pylons.machines.harvester.HarvesterPylonBlock;
import net.permutated.pylons.machines.harvester.HarvesterPylonBlockItem;
import net.permutated.pylons.machines.harvester.HarvesterPylonContainer;
import net.permutated.pylons.machines.harvester.HarvesterPylonTile;
import net.permutated.pylons.machines.infusion.InfusionPylonBlock;
import net.permutated.pylons.machines.infusion.InfusionPylonBlockItem;
import net.permutated.pylons.machines.infusion.InfusionPylonContainer;
import net.permutated.pylons.machines.infusion.InfusionPylonTile;
import net.permutated.pylons.machines.interdiction.InterdictionPylonBlock;
import net.permutated.pylons.machines.interdiction.InterdictionPylonBlockItem;
import net.permutated.pylons.machines.interdiction.InterdictionPylonContainer;
import net.permutated.pylons.machines.interdiction.InterdictionPylonTile;
import net.permutated.pylons.machines.protection.ProtectionPylonBlock;
import net.permutated.pylons.machines.protection.ProtectionPylonBlockItem;
import net.permutated.pylons.machines.protection.ProtectionPylonContainer;
import net.permutated.pylons.machines.protection.ProtectionPylonTile;
import net.permutated.pylons.recipe.HarvestingRecipe;
import net.permutated.pylons.recipe.HarvestingRegistry;
import net.permutated.pylons.util.Constants;
import net.permutated.pylons.util.TranslationKey;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.permutated.pylons.util.ResourceUtil.prefix;

public class ModRegistry {
    private ModRegistry() {
        // nothing to do
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Pylons.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Pylons.MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Pylons.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Pylons.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Pylons.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Pylons.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Pylons.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Pylons.MODID);
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, Pylons.MODID);
    public static final HarvestingRegistry HARVESTING_REGISTRY = new HarvestingRegistry();

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable(TranslationKey.tab()))
        .icon(() -> ModRegistry.PLAYER_FILTER.get().getDefaultInstance())
        .displayItems((parameters, output) -> ITEMS.getEntries().stream()
            .map(Supplier::get)
            .map(Item::getDefaultInstance)
            .forEach(output::accept))
        .build()
    );

    // Tags
    public static final TagKey<Item> HARVESTER_BANNED = ItemTags.create(prefix("harvester_banned"));
    public static final TagKey<MobEffect> INFUSION_BANNED = TagKey.create(Registries.MOB_EFFECT, prefix("infusion_banned"));

    // Items
    public static final Supplier<Item> PLAYER_FILTER = ITEMS.registerItem("player_filter", PlayerFilterCard::new);
    public static final Supplier<Item> POTION_FILTER = ITEMS.registerItem("potion_filter", PotionFilterCard::new);
    public static final Supplier<Item> MOB_FILTER = ITEMS.registerItem("mob_filter", MobFilterCard::new);
    public static final Supplier<Item> LIFELESS_FILTER = ITEMS.registerItem("lifeless_filter", LifelessFilterCard::new);
    public static final Supplier<Item> BLOCK_FILTER = ITEMS.registerItem("block_filter", BlockFilterCard::new);

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

    public static final Supplier<DataComponentType<BlockComponent>> BLOCK_COMPONENT = COMPONENTS.registerComponentType(
        "block", builder -> builder.persistent(BlockComponent.BASIC_CODEC)
    );

    // Blocks
    public static final DeferredBlock<Block> EXPULSION_PYLON = BLOCKS.registerBlock(Constants.EXPULSION_PYLON, ExpulsionPylonBlock::new);
    public static final DeferredBlock<Block> INFUSION_PYLON = BLOCKS.registerBlock(Constants.INFUSION_PYLON, InfusionPylonBlock::new);
    public static final DeferredBlock<Block> HARVESTER_PYLON = BLOCKS.registerBlock(Constants.HARVESTER_PYLON, HarvesterPylonBlock::new);
    public static final DeferredBlock<Block> INTERDICTION_PYLON = BLOCKS.registerBlock(Constants.INTERDICTION_PYLON, InterdictionPylonBlock::new);
    public static final DeferredBlock<Block> PROTECTION_PYLON = BLOCKS.registerBlock(Constants.PROTECTION_PYLON, ProtectionPylonBlock::new);

    // BlockItems
    public static final Supplier<BlockItem> EXPULSION_PYLON_ITEM = blockItem(EXPULSION_PYLON, ExpulsionPylonBlockItem::new);
    public static final Supplier<BlockItem> INFUSION_PYLON_ITEM = blockItem(INFUSION_PYLON, InfusionPylonBlockItem::new);
    public static final Supplier<BlockItem> HARVESTER_PYLON_ITEM = blockItem(HARVESTER_PYLON, HarvesterPylonBlockItem::new);
    public static final Supplier<BlockItem> INTERDICTION_PYLON_ITEM = blockItem(INTERDICTION_PYLON, InterdictionPylonBlockItem::new);
    public static final Supplier<BlockItem> PROTECTION_PYLON_ITEM = blockItem(PROTECTION_PYLON, ProtectionPylonBlockItem::new);

    // Tiles
    public static final Supplier<BlockEntityType<ExpulsionPylonTile>> EXPULSION_PYLON_TILE = blockEntity(EXPULSION_PYLON, ExpulsionPylonTile::new);
    public static final Supplier<BlockEntityType<InfusionPylonTile>> INFUSION_PYLON_TILE = blockEntity(INFUSION_PYLON, InfusionPylonTile::new);
    public static final Supplier<BlockEntityType<HarvesterPylonTile>> HARVESTER_PYLON_TILE = blockEntity(HARVESTER_PYLON, HarvesterPylonTile::new);
    public static final Supplier<BlockEntityType<InterdictionPylonTile>> INTERDICTION_PYLON_TILE = blockEntity(INTERDICTION_PYLON, InterdictionPylonTile::new);
    public static final Supplier<BlockEntityType<ProtectionPylonTile>> PROTECTION_PYLON_TILE = blockEntity(PROTECTION_PYLON, ProtectionPylonTile::new);

    // Containers
    public static final Supplier<MenuType<ExpulsionPylonContainer>> EXPULSION_PYLON_CONTAINER = container(Constants.EXPULSION_PYLON, ExpulsionPylonContainer::new);
    public static final Supplier<MenuType<InfusionPylonContainer>> INFUSION_PYLON_CONTAINER = container(Constants.INFUSION_PYLON, InfusionPylonContainer::new);
    public static final Supplier<MenuType<HarvesterPylonContainer>> HARVESTER_PYLON_CONTAINER = container(Constants.HARVESTER_PYLON, HarvesterPylonContainer::new);
    public static final Supplier<MenuType<InterdictionPylonContainer>> INTERDICTION_PYLON_CONTAINER = container(Constants.INTERDICTION_PYLON, InterdictionPylonContainer::new);
    public static final Supplier<MenuType<ProtectionPylonContainer>> PROTECTION_PYLON_CONTAINER = container(Constants.PROTECTION_PYLON, ProtectionPylonContainer::new);

    // Recipe Types
    public static final Supplier<RecipeType<HarvestingRecipe>> HARVESTING_RECIPE_TYPE = RECIPE_TYPES.register(Constants.HARVESTING, () -> RecipeType.simple(prefix(Constants.HARVESTING)));
    public static final Supplier<RecipeBookCategory> HARVESTING_RECIPE_CATEGORY = RECIPE_BOOK_CATEGORIES.register(Constants.HARVESTING, RecipeBookCategory::new);
    public static final Supplier<RecipeSerializer<HarvestingRecipe>> HARVESTING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(Constants.HARVESTING, () -> HarvestingRecipe.SERIALIZER);

    /**
     * Register a BlockItem for a Block
     *
     * @param holder the Block
     * @return the new registry object
     */
    private static DeferredItem<BlockItem> blockItem(Holder<Block> holder, BiFunction<Block, Item.Properties, BlockItem> supplier) {
        return ITEMS.registerItem(holder.unwrapKey().map(ResourceKey::identifier).map(Identifier::getPath).orElseThrow(),
            properties -> supplier.apply(holder.value(), properties.useBlockDescriptionPrefix()));
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
     * Register a tile entity for a Block
     *
     * @param holder   a Holder containing a Block
     * @param supplier a Supplier that returns the new Block Entity
     * @return the new registry object
     */
    private static <T extends AbstractPylonTile> Supplier<BlockEntityType<T>> blockEntity(Holder<Block> holder, BlockEntityType.BlockEntitySupplier<T> supplier) {
        return TILES.register(holder.unwrapKey().map(ResourceKey::identifier).map(Identifier::getPath).orElseThrow(),
            () -> new BlockEntityType<>(supplier, holder.value()));
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
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_BOOK_CATEGORIES.register(bus);
    }
}
