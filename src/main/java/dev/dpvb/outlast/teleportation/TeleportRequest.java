package dev.dpvb.outlast.teleportation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a request to teleport to another player.
 */
public interface TeleportRequest {
    /**
     * Represents the state of the request.
     */
    enum State {
        /**
         * The request has been sent.
         */
        SENT,
        /**
         * The request has been accepted.
         */
        ACCEPTED,
        /**
         * The request has been denied.
         */
        DENIED,
        /**
         * The request has expired.
         */
        EXPIRED,
    }

    /**
     * Gets the state of the request.
     *
     * @return the state of the request
     */
    State getState();

    /**
     * Gets the player who sent the request.
     *
     * @return the player who sent the request
     */
    @NotNull Player getSender();

    /**
     * Gets the target player of the request.
     *
     * @return the target player of the request
     */
    @NotNull Player getTarget();

    /**
     * Gets the timeout of the request in seconds.
     * <p>
     * After this amount of time the request will expire.
     *
     * @return the timeout of the request
     */
    default long getTimeout() {
        return 60;
    }

    /**
     * Gets the time since the request was created in seconds.
     *
     * @return the seconds since the request was created
     * @see #getTimeout()
     */
    long getTimeSince();

    /**
     * Accepts the request.
     *
     * @return true only if the request was changed from sent to accepted
     */
    boolean accept();
}
