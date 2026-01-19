package net.permutated.pylons.compat.teams;

import net.neoforged.fml.ModList;

public class TeamCompat {
    private TeamCompat() {
        // nothing to do
    }
    private static TeamSupport instance = new NoTeamSupport();
    public static TeamSupport getInstance() {
        return instance;
    }

    public static void init() {
        if (ModList.get().isLoaded("ftbteams")) {
            instance = new FTBTeamSupport();
        }
    }
}
