package dev.dpvb.outlast.teleportation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

/**
 * Handles teleportation.
 */
public interface TeleportService {
    /**
     * Teleports a player to the player's team's home.
     * <p>
     * This method returns null if the player is not in a team or if the
     * player's team does not have a home set.
     *
     * @param player the player to teleport
     * @return a pending teleport or null
     */
    @Nullable PendingTeleport teleportHome(@NotNull Player player);

    /**
     * Requests a teleport to another player.
     *
     * @param player the player who is requesting the teleport
     * @param target the request target
     * @return a teleport request
     */
    @NotNull TeleportRequest requestTeleport(@NotNull Player player, @NotNull Player target);

    /**
     * Gets the pending requests for a player.
     *
     * @param player a player
     * @return the pending requests for the player
     */
    @NotNull Queue<TeleportRequest> getPendingRequests(@NotNull Player player);
}
