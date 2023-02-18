package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.internal.OutlastPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

// runs every second and marks expired requests
class TeleportRequestProcessor extends BukkitRunnable {
    private final Map<Player, TeleportRequest> requestMap = new HashMap<>();

    TeleportRequestProcessor() {
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0, 20);
    }

    @Nullable TeleportRequest getRequest(Player player) {
        synchronized (requestMap) {
            return requestMap.get(player);
        }
    }

    @Nullable TeleportRequest setRequest(Player player, TeleportRequest request) {
        synchronized (requestMap) {
            // clear out requests the sender has made previously.
            requestMap.values().removeIf(tpRequest -> tpRequest.getSender().equals(request.getSender()));
            // add request
            return requestMap.put(player, request);
        }
    }

    @Override
    public void run() {
        synchronized (requestMap) {
            // remove requests of players that are now offline
            requestMap.keySet().removeIf(player -> !player.isOnline());
            // update each player's request queue
            requestMap.forEach((player, request) -> {
                // update expired requests
                if (request.state != TeleportRequest.State.SENT) return;
                if (request.getTimeSince() >= TeleportRequest.TIMEOUT) {
                    request.state = TeleportRequest.State.EXPIRED;
                    request.getSender().sendPlainMessage("Your teleport request to " + player.getName() + " has expired.");
                }
            });
            // remove non sents
            requestMap.values().removeIf(invite -> invite.state != TeleportRequest.State.SENT);
        }
    }
}
