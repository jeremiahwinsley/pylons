package net.permutated.pylons.compat.teams;

import earth.terrarium.argonauts.api.guild.Guild;
import earth.terrarium.argonauts.api.guild.GuildApi;
import java.util.UUID;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ArgonautTeamSupport implements TeamSupport {
    @Override
    public boolean arePlayersInSameTeam(UUID player1, UUID player2) {
        Guild guild = GuildApi.API.getPlayerGuild(ServerLifecycleHooks.getCurrentServer(), player1);

        if (guild != null) {
            return guild.members().isMember(player2);
        }
        return false;
    }
}
