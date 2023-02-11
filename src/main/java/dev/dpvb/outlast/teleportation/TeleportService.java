package dev.dpvb.outlast.teleportation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

/**
 * Handles teleportation.
 */
public class TeleportService {
    private static final TeleportService INSTANCE = new TeleportService();
    private final long channelTicks = 20L * 5;

    /**
     * Teleports a player to the player's team's home.
     * <p>
     * This method returns null if the player is not in a team or if the
     * player's team does not have a home set.
     *
     * @param player the player to teleport
     * @return a channeling teleport or null
     */
    public @Nullable ChannelingTeleport teleportHome(@NotNull Player player) {
        // TODO
        return null;
    }

    /**
     * Requests a teleport to another player.
     *
     * @param player the player who is requesting the teleport
     * @param target the request target
     * @return a teleport request
     */
    public @NotNull TeleportRequest requestTeleport(@NotNull Player player, @NotNull Player target) {
        // TODO FIXME
        return null;
    }

    /**
     * Gets the pending requests for a player.
     *
     * @param player a player
     * @return the pending requests for the player
     */
    public @NotNull Queue<TeleportRequest> getPendingRequests(@NotNull Player player) {
        // TODO FIXME
        return null;
    }

    public static TeleportService getInstance() {
        return INSTANCE;
    }
}
