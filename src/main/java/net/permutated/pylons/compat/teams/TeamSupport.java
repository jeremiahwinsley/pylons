package net.permutated.pylons.compat.teams;

import java.util.UUID;

public interface TeamSupport {
    boolean arePlayersInSameTeam(UUID player1, UUID player2);
}
