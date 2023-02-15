package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.internal.OutlastPlugin;
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
                channeling.ticksWaited++;
                if (channeling.ticksWaited >= CHANNEL_TICKS) {
                    // only process waiting channels
                    if (channeling.state != ChannelingTeleport.State.WAITING) continue;
                    final var teleporting = channeling.getTeleporting();
                    if (!teleporting.isOnline()) {
                        final var success = channeling.execute();
                        if (success) {
                            // teleport succeeded
                            channeling.state = ChannelingTeleport.State.SUCCEEDED;
                        } else {
                            // teleport failed
                            channeling.state = ChannelingTeleport.State.CANCELLED; // FIXME should this be a separate state?
                        }
                    }
                }
            }
            // remove stale channels
            channeling.removeIf(channeling -> channeling.state != ChannelingTeleport.State.WAITING);
        }
    }
}
