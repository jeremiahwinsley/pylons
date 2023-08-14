package net.permutated.pylons.compat.teams;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;

import java.util.UUID;

public class FTBTeamSupport implements TeamSupport {
    @Override
    public boolean arePlayersInSameTeam(UUID player1, UUID player2) {
        return FTBTeamsAPI.arePlayersInSameTeam(player1, player2);
    }
}
