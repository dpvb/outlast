package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.internal.OutlastPlugin;
import dev.dpvb.outlast.messages.Messages;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// runs every second and marks expired invites
class TeamInviteProcessor extends BukkitRunnable {
    // maps sender to current invite
    private final Map<Player, TeamInvite> inviteMap = new HashMap<>();

    TeamInviteProcessor() {
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0, 20);
    }

    @Nullable TeamInvite getInvite(Player player) {
        synchronized (inviteMap) {
            return inviteMap.get(player);
        }
    }

    @Nullable TeamInvite setInvite(Player player, TeamInvite invite) {
        synchronized (inviteMap) {
            return inviteMap.put(player, invite);
        }
    }


    @Override
    public void run() {
        synchronized (inviteMap) {
            // update each player's invite queue
            inviteMap.forEach((player, invite) -> {
                // update expired invites
                if (invite.state != TeamInvite.State.SENT) return;
                if (invite.getTimeSince() >= TeamInvite.TIMEOUT) {
                    invite.state = TeamInvite.State.EXPIRED;
                    Messages.game("leader.invite.sent.expiration").send(player);
                }
            });
            // remove non sents
            inviteMap.values().removeIf(invite -> invite.state != TeamInvite.State.SENT);
        }
    }
}
