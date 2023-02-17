package dev.dpvb.outlast.teams;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * A request to join to a team.
 */
public class TeamRequest {
    /**
     * The seconds it takes for a team request to expire.
     */
    public static final long TIMEOUT = 300; // 5 minutes

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
    private final String teamName;
    @NotNull State state = State.SENT;

    public TeamRequest(@NotNull Player sender, @NotNull String teamName) {
        this.sender = sender;
        this.teamName = teamName;
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
     * Gets the name of the team the request is for.
     *
     * @return the name of the team
     */
    public @NotNull String getTeamName() {
        return teamName;
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
     * This method will only return true if the request has not already been
     * accepted, denied or has expired.
     *
     * @return true if the request was accepted successfully
     */
    public boolean accept() {
        if (state != State.SENT) {
            return false;
        }
        state = State.ACCEPTED;
        return true;
    }

    /**
     * Denies the request.
     * <p>
     * This method will only return true if the request has not already been
     * accepted, denied or has expired.
     *
     * @return true if the request was denied successfully
     */
    public boolean deny() {
        if (state != State.SENT) {
            return false;
        }
        state = State.DENIED;
        return true;
    }
}
