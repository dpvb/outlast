package dev.dpvb.outlast.teleportation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;

/**
 * A request to teleport to another player.
 */
public class TeleportRequest {
    /**
     * The seconds it takes for a teleport request to expire.
     */
    public static final long TIMEOUT = 60;

    /**
     * Represents the state of a request.
     */
    public enum State {
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

    private final Instant instant = Instant.now();
    private final Player sender;
    private final Player target;
    private @NotNull State state = State.SENT;

    public TeleportRequest(@NotNull Player sender, @NotNull Player target) {
        this.sender = sender;
        this.target = target;
    }

    /**
     * Gets the state of the request.
     *
     * @return the state of the request
     */
    public @NotNull State getState() {
        return state;
    }

    /**
     * Gets the player who sent the request.
     *
     * @return the player who sent the request
     */
    public @NotNull Player getSender() {
        return sender;
    }

    /**
     * Gets the target player of the request.
     *
     * @return the target player of the request
     */
    public @NotNull Player getTarget() {
        return target;
    }

    /**
     * Gets the time since the request was created in seconds.
     *
     * @return the seconds since the request was created
     */
    public long getTimeSince() {
        return Instant.now().getEpochSecond() - instant.getEpochSecond();
    }

    /**
     * Accepts the request.
     * <p>
     * This method will return an empty optional if the request has already
     * been accepted, denied or has expired; otherwise it will contain the
     * resulting teleport channel.
     *
     * @return an optional describing the resultant teleport channel
     */
    public @NotNull Optional<ChannelingTeleport> accept() {
        if (state != State.SENT) {
            return Optional.empty();
        }
        state = State.ACCEPTED;
        return Optional.of(new ChannelingTeleport.PlayerChannel(sender, target));
    }
}
