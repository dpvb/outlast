package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.cache.TeamCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeamService {
    private static final TeamService INSTANCE = new TeamService();
    private PlayerCache playerCache;
    private TeamCache teamCache;

    private TeamService() {}

    /**
     * Gets the team of a player.
     *
     * @param player a player
     * @return the team of the player or null if the player is not in a team
     */
    public @Nullable String getTeam(@NotNull UUID player) {
        if (playerCache == null) throw new IllegalStateException("playerCache not initialized");
        final var model = playerCache.getModel(player);
        if (model == null) return null;
        return model.getTeam_name();
    }

    /**
     * Creates a new team.
     * <p>
     * If a team with the given name already exists, this method returns false.
     *
     * @param teamName the name of the new team
     * @param leader the leader of the new team
     * @return true unless a team with name {@code teamName} already exists
     */
    public boolean createTeam(@NotNull String teamName, @NotNull UUID leader) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (teamCache.getModel(teamName) != null) {
            return false;
        }
        // TODO finish impl
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a player to a team, removing them as needed.
     * <p>
     * If removing the player from their current team (if present) would cause
     * team destruction this method should throw an exception. If
     * {@code teamName} does not exist, this method should return false.
     *
     * @param teamName the name of the team to join
     * @param player the player to add
     * @return true if the team exists and the player was added
     */
    public boolean joinTeam(@NotNull String teamName, @NotNull UUID player) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (playerCache == null) throw new IllegalStateException("playerCache not initialized");
        // TODO finish this later (need to implement request subsystem)
        throw new UnsupportedOperationException();
    }

    /**
     * Removes a player from their team.
     * <p>
     * This method removes the player from the team and removes the team if
     * removing the player leaves the team with no members. It also reassigns
     * the team's leader if {@code player} is the current leader.
     * <p>
     * This method returns false if the player is not in a team.
     *
     * @param player the player to remove
     * @return true unless the player was not in a team
     */
    public boolean leaveTeam(@NotNull UUID player) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (playerCache == null) throw new IllegalStateException("playerCache not initialized");
        // TODO finish impl
        throw new UnsupportedOperationException();
    }

    // call this after SQLService is initialized
    public void setup() {
        // capture cache instances
        playerCache = SQLService.getInstance().getPlayerCache();
        teamCache = SQLService.getInstance().getTeamCache();
    }

    public static TeamService getInstance() {
        return INSTANCE;
    }
}
