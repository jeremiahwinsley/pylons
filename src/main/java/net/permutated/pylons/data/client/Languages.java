package net.permutated.pylons.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.permutated.pylons.ModRegistry;
import net.permutated.pylons.Pylons;

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
            add("gui.pylons.owner", "Owner: %s");
            add("gui.pylons.whitelist", "Add players to whitelist:");
        }
    }
}
