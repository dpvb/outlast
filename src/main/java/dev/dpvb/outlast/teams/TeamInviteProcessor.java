package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.internal.OutlastPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// runs every second and marks expired invites
class TeamInviteProcessor extends BukkitRunnable {
    // maps sender to current invite // FIXME use a list
    private final Map<Player, TeamInvite> inviteMap = new HashMap<>();

    TeamInviteProcessor() {
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0, 20);
    }

    @Nullable TeamInvite getInvites(Player player) {
        synchronized (inviteMap) {
            return inviteMap.get(player);
        }
    }

    @Nullable TeamInvite putInvite(Player player, TeamInvite invite) {
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
                }
                // notify accepts+declines
                switch (invite.state) {
                    // FIXME localize message
                    case ACCEPTED -> player.sendMessage("Your invite to join the team was accepted by " + invite.getInvitee().getName() + ".");
                    // FIXME localize message
                    case DECLINED -> player.sendMessage("Your invite to join the team was declined by " + invite.getInvitee().getName() + ".");
                }
            });
            // remove accepts+declines
            inviteMap.values().removeIf(invite -> invite.state == TeamInvite.State.DECLINED || invite.state == TeamInvite.State.ACCEPTED);
        }
    }
}
