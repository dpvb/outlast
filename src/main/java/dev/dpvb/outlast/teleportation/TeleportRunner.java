package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.internal.OutlastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

// runs every tick and advances each channel in queue by 1
class TeleportRunner extends BukkitRunnable {
    private static final long CHANNEL_TICKS = 20L * 5;
    private final Set<ChannelingTeleport> channeling = new HashSet<>();

    TeleportRunner() {
        // run every tick
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0L, 1L);
    }

    void add(ChannelingTeleport teleport) {
        synchronized (channeling) {
            channeling.add(teleport);
        }
    }

    @Override
    public void run() {
        synchronized (channeling) {
            // update each channel
            for (ChannelingTeleport channeling : channeling) {
                final var teleporting = channeling.getTeleporting();
                // send message to player notifying time left on teleport.
                final var timeLeft = CHANNEL_TICKS - channeling.ticksWaited;
                if (timeLeft % 20 == 0 && timeLeft / 20 != 0) {
                    teleporting.sendMessage("Teleporting in " + (timeLeft / 20) + " seconds.");
                }
                channeling.ticksWaited++;
                if (channeling.ticksWaited >= CHANNEL_TICKS) {
                    // only process waiting channels
                    if (channeling.state != ChannelingTeleport.State.WAITING) continue;
                    // check if player is still online and teleport them.
                    if (teleporting.isOnline()) {
                        final var success = channeling.execute();
                        if (success) {
                            // teleport succeeded
                            channeling.state = ChannelingTeleport.State.SUCCEEDED;
                            teleporting.sendMessage("Teleported!");
                        } else {
                            // teleport failed
                            channeling.state = ChannelingTeleport.State.CANCELLED; // FIXME should this be a separate state?
                        }
                    } else {
                        // if player goes offline the ChannelingTeleport is cancelled.
                        channeling.state = ChannelingTeleport.State.CANCELLED;
                    }

                }
            }
            // remove stale channels
            channeling.removeIf(channeling -> channeling.state != ChannelingTeleport.State.WAITING);
        }
    }
}
