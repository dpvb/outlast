package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.internal.OutlastPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

// runs every second and marks expired requests
class TeleportRequestProcessor extends BukkitRunnable {
    private final Map<Player, LinkedList<TeleportRequest>> requestMap = new HashMap<>();

    TeleportRequestProcessor() {
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0, 20);
    }

    Queue<TeleportRequest> getRequests(Player player) {
        synchronized (requestMap) {
            return requestMap.compute(player, (p, requests) -> {
                if (requests == null) return new LinkedList<>();
                return requests;
            });
        }
    }

    @Override
    public void run() {
        synchronized (requestMap) {
            // remove requests of players that are now offline
            requestMap.keySet().removeIf(player -> !player.isOnline());
            // update each player's request queue
            requestMap.forEach((player, requests) -> {
                // update expired requests
                for (TeleportRequest request : requests) {
                    if (request.state != TeleportRequest.State.SENT) continue;
                    if (request.getTimeSince() >= TeleportRequest.TIMEOUT) {
                        request.state = TeleportRequest.State.EXPIRED;
                    }
                }
            });
        }
    }
}
