package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.messages.Messages;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A teleport in progress.
 */
public abstract class ChannelingTeleport {
    /**
     * Represents the state of a teleport.
     */
    @SuppressWarnings("UnnecessarySemicolon")
    public enum State {
        /**
         * The target is waiting to teleport.
         */
        WAITING,
        /**
         * The target will not be teleported.
         */
        CANCELLED,
        /**
         * The target has been teleported.
         */
        SUCCEEDED,
        ;
    }

    protected final Player teleporting;
    @NotNull State state = State.WAITING;
    long ticksWaited;

    ChannelingTeleport(@NotNull Player teleporting) {
        this.teleporting = teleporting;
    }

    /**
     * Gets the state of the teleport.
     *
     * @return the state of the teleport
     */
    public @NotNull State getState() {
        return state;
    }

    /**
     * Gets the player who will be teleporting.
     *
     * @return the player who will be teleporting
     */
    public @NotNull Player getTeleporting() {
        return teleporting;
    }

    /**
     * Executes the teleport.
     * <p>
     * This method should return true on successful teleport, only returning
     * false if the teleport cannot be completed due to a change in game state;
     * for instance, if the destination player is no longer online or the
     * target's team's home is no longer set.
     *
     * @return true unless an error occurred
     */
    abstract boolean execute();

    /**
     * Cancels the teleport.
     *
     * @return true unless the teleport has already been cancelled or succeeded
     */
    public boolean cancel() {
        if (state == State.WAITING) {
            state = State.CANCELLED;
            Messages.game("tp.cancelled.unspecified").send(teleporting);
            return true;
        }
        return false;
    }

    static class PlayerChannel extends ChannelingTeleport {
        private final Player destination;

        PlayerChannel(@NotNull Player teleporting, @NotNull Player destination) {
            super(teleporting);
            this.destination = destination;
        }

        @Override
        boolean execute() {
            if (!destination.isOnline()) return false;
            return teleporting.teleport(destination);
        }
    }

    static class LocationChannel extends ChannelingTeleport {
        private final Location destination;

        LocationChannel(@NotNull Player teleporting, @NotNull Location destination) {
            super(teleporting);
            this.destination = destination;
        }

        @Override
        boolean execute() {
            return teleporting.teleport(destination);
        }
    }
}
