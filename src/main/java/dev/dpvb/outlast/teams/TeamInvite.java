package dev.dpvb.outlast.teams;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * An invitation to join a team.
 */
public class TeamInvite {
    /**
     * The seconds it takes for a team invite to expire.
     */
    public static final long TIMEOUT = 300; // 5 minutes

    /**
     * Represents the state of an invitation.
     */
    public enum State {
        /**
         * The invite has been sent.
         */
        SENT,
        /**
         * The invite has been accepted.
         */
        ACCEPTED,
        /**
         * The invite has been declined.
         */
        DECLINED,
        /**
         * The invite has expired.
         */
        EXPIRED,
    }

    private final Instant instant = Instant.now();
    private final Player invitee;
    private final String teamName;
    @NotNull State state = State.SENT;

    public TeamInvite(@NotNull Player invitee, @NotNull String teamName) {
        this.invitee = invitee;
        this.teamName = teamName;
    }

    /**
     * Gets the state of the invitation.
     *
     * @return the state of the invitation
     */
    public @NotNull State getState() {
        return state;
    }

    /**
     * Gets the player being invited.
     *
     * @return the player being invited
     */
    public @NotNull Player getInvitee() {
        return invitee;
    }

    /**
     * Gets the name of the team the invitation is from.
     *
     * @return the name of the team
     */
    public @NotNull String getTeamName() {
        return teamName;
    }

    /**
     * Gets the time since the invitation was created in seconds.
     *
     * @return the seconds since the invitation was created
     */
    public long getTimeSince() {
        return Instant.now().getEpochSecond() - instant.getEpochSecond();
    }

    /**
     * Accepts the invitation.
     * <p>
     * This method will only return true if the invite has not already been
     * accepted, denied or has expired.
     *
     * @return true if the invitation was accepted successfully
     */
    public boolean accept() {
        if (state != State.SENT) {
            return false;
        }
        state = State.ACCEPTED;
        return true;
    }

    /**
     * Declines the invitation.
     * <p>
     * This method will only return true if the invite has not already been
     * accepted, declined or has expired.
     *
     * @return true if the invitation was declined successfully
     */
    public boolean decline() {
        if (state != State.SENT) {
            return false;
        }
        state = State.DECLINED;
        return true;
    }
}
