package dev.dpvb.outlast.teleportation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A teleport in progress.
 */
public class ChannelingTeleport {
    /**
     * Represents the state of a teleport.
     */
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

    private final Supplier<Location> locationSupplier;
    private @NotNull State state = State.WAITING;

    public ChannelingTeleport(@NotNull Supplier<Location> locationSupplier) {
        this.locationSupplier = locationSupplier;
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
     * Gets the location function of the teleport destination.
     *
     * @return the teleport destination as a location function
     */
    public @NotNull Supplier<Location> getDestination() {
        return locationSupplier;
    }

    /**
     * Cancels the teleport.
     *
     * @return true unless the teleport has already been cancelled or succeeded
     */
    public boolean cancel() {
        // TODO
        return false;
    }
}
