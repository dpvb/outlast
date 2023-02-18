package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.internal.OutlastPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

// runs every second and marks expired invites
class TeamInviteProcessor extends BukkitRunnable {
    // maps sender to current invite // FIXME use a list
    private final Map<Player, LinkedList<TeamInvite>> inviteMap = new HashMap<>();

    TeamInviteProcessor() {
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0, 20);
    }

    Queue<TeamInvite> getInvites(Player player) {
        synchronized (inviteMap) {
            return inviteMap.compute(player, (p, invites) -> {
                if (invites == null) return new LinkedList<>();
                return invites;
            });
        }
    }

    @Override
    public void run() {
        synchronized (inviteMap) {
            // update each player's invite queue
            inviteMap.forEach((player, invites) -> {
                for (TeamInvite invite : invites) {
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
                }
                // remove accepts+declines
                invites.removeIf(invite -> invite.state == TeamInvite.State.DECLINED || invite.state == TeamInvite.State.ACCEPTED);
            });
        }
    }
}
