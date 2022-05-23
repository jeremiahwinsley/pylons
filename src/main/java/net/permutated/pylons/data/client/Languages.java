package net.permutated.pylons.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

import static net.permutated.pylons.util.TranslationKey.*;

public class Languages {
    private Languages() {
        // nothing to do
    }

    public static class English extends LanguageProvider {

        public English(DataGenerator gen) {
            super(gen, Pylons.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            addBlock(ModRegistry.EXPULSION_PYLON, "Expulsion Pylon");
            addItem(ModRegistry.PLAYER_FILTER, "Player Filter");

            addBlock(ModRegistry.INFUSION_PYLON, "Infusion Pylon");
            addItem(ModRegistry.POTION_FILTER, "Potion Filter");

            addBlock(ModRegistry.HARVESTER_PYLON, "Harvester Pylon");

            addBlock(ModRegistry.INTERDICTION_PYLON, "Interdiction Pylon");
            addItem(ModRegistry.MOB_FILTER, "Mob Filter");

            add(gui("owner"), "Owner: %s");
            add(gui("noOwner"), "Owner not found. Pylon disabled.");
            add(gui("wrongDimension"), "This dimension is disabled.");
            add(gui("insideWorldSpawn"), "Too close to world spawn.");
            add(gui("toolMissing"), "Tool required for operation.");
            add(gui("inventoryMissing"), "Place inventory above pylon.");
            add(gui("inventoryFull"), "Inventory is full.");
            add(gui("working"), "Pylon is working.");
            add(gui("whitelist"), "Add players to whitelist:");
            add(gui("blockedMobs"), "Add mobs to prevent spawns:");
            add(gui("effects"), "Active potion effects:");
            add(gui("workArea"), "Work area (in chunks)");
            add(gui("workAreaBlocks"), "Work area (in blocks)");
            add(gui("toggleWork"), "Working status");
            add(tab(), "Pylons");

            add(chat("expelled"), "You have been expelled from %s's chunk!");

            add(tooltip("expulsion1"), "Expels other players from");
            add(tooltip("expulsion2"), "the chunk where the pylon");
            add(tooltip("expulsion3"), "is placed.");

            add(tooltip("infusion1"), "Applies potion effects from");
            add(tooltip("infusion2"), "an activated potion filter");
            add(tooltip("infusion3"), "at any distance.");

            add(tooltip("harvester1"), "Harvests crops in a configurable");
            add(tooltip("harvester2"), "radius around the pylon. Just place");
            add(tooltip("harvester3"), "inside the water block.");

            add(tooltip("interdiction1"), "Configurable mob spawn prevention");
            add(tooltip("interdiction2"), "in a radius around the pylon.");

            add(tooltip("no_player"), "Right-click on a player to select them.");
            add(tooltip("no_mob"), "Right-click on a mob to select it.");

            add(tooltip("no_effect1"), "Right-click with an active effect");
            add(tooltip("no_effect2"), "to apply it to the card.");
            add(tooltip("minimum_duration"), "Minimum effect duration: %s seconds");

            add(tooltip("effect_denied"), "Effect is disabled in the config.");

            add(tooltip("insert1"), "Insert this filter into");
            add(tooltip("insert2"), "a pylon to use it!");

            add(tooltip("increase1"), "Right-click with the same effect");
            add(tooltip("increase2"), "active to increase progress.");

            add(tooltip("activated"), "Activated");
            add(tooltip("progress"), "Progress: %d/%d seconds");

            add(tooltip("player"), "Player: %s");
        }
    }
}
