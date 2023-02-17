package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.cache.TeamCache;
import dev.dpvb.outlast.sql.models.SQLPlayer;
import dev.dpvb.outlast.sql.models.SQLTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamService {
    private static final TeamService INSTANCE = new TeamService();
    private static final int TEAM_LIMIT = 3;
    private PlayerCache playerCache;
    private TeamCache teamCache;
    private TeamRequestProcessor requestProcessor;

    private TeamService() {}

    /**
     * Gets the team of a player.
     *
     * @param player a player
     * @return the team of the player or null if the player is not in a team
     */
    public @Nullable String getTeam(@NotNull UUID player) {
        return getPlayerModel(player).getTeam_name();
    }

    private @NotNull SQLPlayer getPlayerModel(@NotNull UUID player) {
        if (playerCache == null) throw new IllegalStateException("playerCache not initialized");
        // prevent operation if player does not have a valid model
        // (this ensures that player is a valid player uuid)
        return Objects.requireNonNull(playerCache.getModel(player), "player does not have a valid model");
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
        // return false if a team already exists with this name
        if (getTeamModel(teamName) != null) {
            return false;
        }
        // remove the leader from their current team if needed
        final var leaderModel = getPlayerModel(leader);
        if (leaderModel.getTeam_name() != null) {
            if (!leaveTeam(leader)) {
                throw new IllegalStateException("Unable to create team as unable to leave team");
            }
        }
        // create the team
        teamCache.createModel(teamName, sqlTeam -> {
            sqlTeam.setLeader(leader);
        });
        // set the team leader for the leader
        playerCache.updateModel(leader, sqlPlayer -> {
            sqlPlayer.setTeam_name(teamName);
        });
        return true;
    }

    private @Nullable SQLTeam getTeamModel(@NotNull String teamName) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        return teamCache.getModel(teamName);
    }

    /**
     * Adds a player to a team.
     * <p>
     * If {@code teamName} does not exist or the team is full, this method should return false.
     *
     * @param player the player to add
     * @return true if the team exists, there was space, and the player was added
     */
    public boolean joinTeam(@NotNull String teamName, @NotNull UUID player) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (playerCache == null) throw new IllegalStateException("playerCache not initialized");

        // check if team exists
        final SQLTeam team = getTeamModel(teamName);
        if (team == null) {
            return false;
        }

        // check if team is full
        if (isTeamFull(teamName)) {
            return false;
        }

        // add player to team in db
        playerCache.updateModel(player, sqlPlayer -> {
            sqlPlayer.setTeam_name(teamName);
        });
        return true;
    }


    public TeamRequest invitePlayer(@NotNull Player sender, @NotNull String teamName, @NotNull Player target) {
        // create request
        final TeamRequest request = new TeamRequest(sender, teamName, target);
        requestProcessor.putRequest(target, request);
        return request;
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

        // get the player's team name
        final String teamName = getTeam(player);
        if (teamName == null) {
            // they have no team
            return false;
        }

        // Get a copy of all members in the team
        final List<UUID> members = getTeamMembers(teamName);
        // Remove the player from the list
        members.remove(player);

        // unset the team of the player
        playerCache.updateModel(player, sqlPlayer -> {
            sqlPlayer.setTeam_name(null);
        });

        // if the team without the player has 0 members left, delete the team
        if (members.size() == 0) {
            teamCache.deleteModel(teamName);
        } else {
            // check if the player leaving is the current leader of the team
            if (teamCache.getModel(teamName).getLeader().equals(player)) {
                // give team leader to someone else
                teamCache.updateModel(teamName, sqlTeam -> {
                    sqlTeam.setLeader(members.get(0));
                });
            }
        }

        return true;
    }

    /**
     * Gets the members of a team.
     * <p>
     * If the returned is empty it means {@code teamName} does not exist (as
     * a team cannot exist without members).
     *
     * @param teamName the name of the team
     * @return a list of team members. May be empty.
     */
    public @NotNull List<UUID> getTeamMembers(@NotNull String teamName) {
        if (playerCache == null) throw new IllegalStateException("playerCache not initialized");
        return playerCache.getModels().stream()
                .filter(sqlPlayer -> teamName.equals(sqlPlayer.getTeam_name()))
                .map(SQLPlayer::getPlayer_uuid)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the provided player is in fact the leader of the named team.
     * <p>
     * This method returns true only if 1) {@code teamName} exists and 2) its
     * leader is {@code player}.
     *
     * @param player the player's uuid
     * @param teamName the name of the team to check
     * @return true if {@code player} is the leader of team {@code teamName}
     */
    public boolean isLeaderOfTeam(@NotNull UUID player, @NotNull String teamName) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        final SQLTeam team = teamCache.getModel(teamName);
        if (team == null) {
            return false;
        }
        return team.getLeader().equals(player);
    }

    public @Nullable TeamRequest getTeamRequest(@NotNull Player player) {
        return requestProcessor.getRequests(player);
    }

    public boolean isTeamFull(@NotNull String teamName) {
        return getTeamMembers(teamName).size() == TEAM_LIMIT;
    }

    // call this after SQLService is initialized
    public void setup() {
        // capture cache instances
        playerCache = SQLService.getInstance().getPlayerCache();
        teamCache = SQLService.getInstance().getTeamCache();
        // set up request processor
        if (requestProcessor != null && !requestProcessor.isCancelled()) {
            requestProcessor.cancel();
        }
        requestProcessor = new TeamRequestProcessor();
    }

    public static TeamService getInstance() {
        return INSTANCE;
    }
}
