package net.permutated.pylons.compat.teams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;

import java.util.UUID;

public class FTBTeamSupport implements TeamSupport {
    @Override
    public boolean arePlayersInSameTeam(UUID player1, UUID player2) {
        return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(player1, player2);
    }
}
