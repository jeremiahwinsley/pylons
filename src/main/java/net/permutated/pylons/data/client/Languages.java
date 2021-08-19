package net.permutated.pylons.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;
import net.permutated.pylons.util.TranslationKey;

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
            add(TranslationKey.gui("owner"), "Owner: %s");
            add(TranslationKey.gui("noOwner"), "Owner not found. Pylon disabled.");
            add(TranslationKey.gui("whitelist"), "Add players to whitelist:");
            add(TranslationKey.tab(), "Pylons");
        }
    }
}
