package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.cache.TeamCache;
import dev.dpvb.outlast.sql.models.SQLPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        // return false if a team already exists with this name
        if (teamCache.getModel(teamName) != null) {
            return false;
        }
        // create the team
        teamCache.createModel(teamName, sqlTeam -> {
            sqlTeam.setLeader(leader);
        });
        // add the leader to the team
        playerCache.updateModel(leader, sqlPlayer -> {
            sqlPlayer.setTeam_name(teamName);
        });
        return true;
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

        final String teamName = getTeam(player);
        if (teamName == null) {
            // The team does not exist
            return false;
        }

        // get the members of the team.
        final List<UUID> members = getTeamMembers(teamName);
        members.remove(player);

        // remove player from the team
        playerCache.updateModel(player, sqlPlayer -> {
            sqlPlayer.setTeam_name(null);
        });

        // if you have 0 teammates, delete the team
        if (members.size() == 0) {
            teamCache.deleteModel(teamName);
        } else {
            // check if the team leader is the player who is leading
            final UUID leader = teamCache.getModel(teamName).getLeader();
            if (leader.equals(player)) {
                // give team leader to someone else
                teamCache.updateModel(teamName, sqlTeam -> {
                    sqlTeam.setLeader(members.get(0));
                });
            }
        }

        return true;
    }

    /**
     * Gets the Members of a Team.
     *
     * If the List is empty, it means the Team does not exist (because no Team can exist without members)
     * @param teamName The name of the team.
     * @return List of UUIDs of Players on the Team. Empty if the Team does not exist.
     */
    private @NotNull List<UUID> getTeamMembers(String teamName) {
        return playerCache.getModels().stream()
                .filter(sqlPlayer -> teamName.equals(sqlPlayer.getTeam_name()))
                .map(SQLPlayer::getPlayer_uuid)
                .collect(Collectors.toList());
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
