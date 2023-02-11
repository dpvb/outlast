package dev.dpvb.outlast.teleportation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Represents a teleport in progress.
 */
public interface ChannelingTeleport {
    /**
     * Represents the state of a teleport.
     */
    enum State {
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

    /**
     * Gets the state of the teleport.
     *
     * @return the state of the teleport
     */
    State getState();

    /**
     * Gets the location function of the teleport destination.
     *
     * @return the teleport destination as a location function
     */
    @NotNull Supplier<Location> getDestination();

    /**
     * Cancels the teleport.
     *
     * @return true unless the teleport has already been cancelled or succeeded
     */
    boolean cancel();
}
