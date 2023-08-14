package net.permutated.pylons.compat.teams;

import java.util.UUID;

public class NoTeamSupport implements TeamSupport {
    @Override
    public boolean arePlayersInSameTeam(UUID player1, UUID player2) {
        return false;
    }
}
