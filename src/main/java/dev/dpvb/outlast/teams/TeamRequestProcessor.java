package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.internal.OutlastPlugin;
import dev.dpvb.outlast.teleportation.TeleportRequest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// runs every second and marks expired requests
class TeamRequestProcessor extends BukkitRunnable {
    // maps sender to current request
    private final Map<Player, TeamRequest> requestMap = new HashMap<>();

    TeamRequestProcessor() {
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0, 20);
    }

    @Nullable TeamRequest getRequests(Player player) {
        synchronized (requestMap) {
            return requestMap.get(player);
        }
    }

    @Nullable TeamRequest putRequest(Player player, TeamRequest request) {
        synchronized (requestMap) {
            return requestMap.put(player, request);
        }
    }

    @Override
    public void run() {
        synchronized (requestMap) {
            // update each player's request queue
            requestMap.forEach((player, request) -> {
                // update expired requests
                if (request.state != TeamRequest.State.SENT) return;
                if (request.getTimeSince() >= TeleportRequest.TIMEOUT) {
                    request.state = TeamRequest.State.EXPIRED;
                }
                // notify accepts+denies
                switch (request.state) {
                    // FIXME localize message
                    case ACCEPTED -> player.sendMessage("Your request to join " + request.getTeamName() + " was accepted.");
                    // FIXME localize message
                    case DENIED -> player.sendMessage("Your request to join " + request.getTeamName() + " was denied.");
                }
            });
            // remove accepts+denies
            requestMap.values().removeIf(request -> request.state == TeamRequest.State.DENIED || request.state == TeamRequest.State.ACCEPTED);
        }
    }
}
