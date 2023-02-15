package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.internal.OutlastPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

// runs every tick and advances each channel in queue by 1
class TeleportRunner extends BukkitRunnable implements Listener {
    private static final long CHANNEL_TICKS = 20L * 5;
    private final Set<ChannelingTeleport> channeling = new HashSet<>();

    TeleportRunner() {
        // run every tick
        runTaskTimer(OutlastPlugin.getPlugin(OutlastPlugin.class), 0L, 1L);
        // register listener
        Bukkit.getPluginManager().registerEvents(this, OutlastPlugin.getPlugin(OutlastPlugin.class));
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

    @EventHandler
    public void onPlayerMoveLocation(PlayerMoveEvent event) {
        synchronized (channeling) {
            if (channeling.size() == 0) {
                return;
            }

            final Player player = event.getPlayer();
            final ChannelingTeleport channel = playerChanneling(player);
            if (channel == null) {
                return;
            }

            if (!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
                channel.cancel();
            }
        }
    }

    /**
     * Check if a Player is Channeling
     * @param player The player to check
     * @return ChannelingTeleport if the player is channeling and the state is waiting, otherwise null
     */
    private @Nullable ChannelingTeleport playerChanneling(@NotNull Player player) {
        synchronized (channeling) {
            for (ChannelingTeleport channeling : channeling) {
                if (channeling.teleporting.equals(player) && channeling.state == ChannelingTeleport.State.WAITING) {
                    return channeling;
                }
            }
            return null;
        }
    }
}
