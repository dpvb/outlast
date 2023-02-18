package dev.dpvb.outlast.teams;

import org.jetbrains.annotations.NotNull;

/**
 * Communicates an error that occurred while performing a team operation.
 */
@SuppressWarnings("serial")
public abstract class TeamError extends Exception {
    TeamError(@NotNull String message) {
        super(message);
    }

    /**
     * The team cannot be created because it already exists.
     */
    public static class Exists extends TeamError {
        Exists(@NotNull String name) {
            super("A team by the name '" + name + "' already exists");
        }
    }

    /**
     * The team cannot be joined because it does not exist.
     */
    public static class DoesNotExist extends TeamError {
        DoesNotExist(@NotNull String name) {
            super("A team by the name '" + name + "' does not exist");
        }
    }

    /**
     * The player cannot create a team because they are already in one.
     */
    public static class PlayerAlreadyTeamed extends TeamError {
        PlayerAlreadyTeamed(@NotNull String currentTeam) {
            super("Player is already on team '" + currentTeam + "'");
        }
    }

    /**
     * The player cannot leave a team because they are not in one.
     */
    public static class PlayerNotTeamed extends TeamError {
        PlayerNotTeamed() {
            super("Player is not on a team");
        }
    }

    /**
     * The player cannot be added to a team because the team is full.
     */
    public static class Full extends TeamError {
        Full() {
            super("The team is full");
        }
    }
}
