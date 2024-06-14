package net.permutated.pylons.compat.teams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.client.ClientTeamManager;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import java.util.UUID;
import net.neoforged.fml.util.thread.EffectiveSide;

public class FTBTeamSupport implements TeamSupport {
    @Override
    public boolean arePlayersInSameTeam(UUID player1, UUID player2) {
        if (EffectiveSide.get().isServer()) {
            return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(player1, player2);
        } else {
            // arePlayersInSameTeam is not implemented in the ClientTeamManager
            // adapted from https://github.com/FTBTeam/FTB-Teams/blob/24f2e4551101477cda8268f6c1b715b168707cb1/common/src/main/java/dev/ftb/mods/ftbteams/data/TeamManagerImpl.java#L133
            ClientTeamManager manager = FTBTeamsAPI.api().getClientManager();
            UUID team1 = manager.getKnownPlayer(player1)
                .map(KnownClientPlayer::teamId)
                .orElse(null);

            UUID team2 = manager.getKnownPlayer(player2)
                .map(KnownClientPlayer::teamId)
                .orElse(null);

            return team1 != null && team1.equals(team2);
        }
    }
}
