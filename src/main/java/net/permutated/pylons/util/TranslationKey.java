package net.permutated.pylons.util;

import net.permutated.pylons.Pylons;

public class TranslationKey {
    private TranslationKey() {
        // nothing to do
    }

    private static final String FORMAT = "%s." + Pylons.MODID + ".%s";

    public static String block(String key) {
        return String.format(FORMAT, "block", key);
    }

    public static String item(String key) {
        return String.format(FORMAT, "item", key);
    }

    public static String gui(String key) {
        return String.format(FORMAT, "gui", key);
    }

    public static String tab() {
        return String.format("itemGroup.%s", Pylons.MODID);
    }

    public static String jei(String key) {
        return String.format(Pylons.MODID + ".int.jei.category.%s", key);
    }
}
