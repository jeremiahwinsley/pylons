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

            add(gui("owner"), "Owner: %s");
            add(gui("noOwner"), "Owner not found. Pylon disabled.");
            add(gui("wrongDimension"), "This dimension is disabled.");
            add(gui("insideWorldSpawn"), "Too close to world spawn.");
            add(gui("whitelist"), "Add players to whitelist:");
            add(gui("effects"), "Active potion effects:");
            add(tab(), "Pylons");

            add(chat("expelled"), "You have been expelled from %s's chunk!");

            add(tooltip("expulsion1"), "Expels other players from");
            add(tooltip("expulsion2"), "the chunk where the pylon");
            add(tooltip("expulsion3"), "is placed.");

            add(tooltip("infusion1"), "Applies potion effects from");
            add(tooltip("infusion2"), "an activated potion filter");
            add(tooltip("infusion3"), "at any distance.");

            add(tooltip("no_player"), "Right-click on a player to select them.");

            add(tooltip("no_effect1"), "Right-click with an active effect");
            add(tooltip("no_effect2"), "to apply it to the card.");
            add(tooltip("minimum_duration"), "Minimum effect duration: %s seconds");

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
