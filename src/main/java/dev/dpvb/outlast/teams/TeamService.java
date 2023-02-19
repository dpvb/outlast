package dev.dpvb.outlast.teams;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.LocationCache;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.cache.TeamCache;
import dev.dpvb.outlast.sql.models.SQLLocation;
import dev.dpvb.outlast.sql.models.SQLPlayer;
import dev.dpvb.outlast.sql.models.SQLTeam;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.dpvb.outlast.teams.TeamError.*;

public class TeamService {
    private static final TeamService INSTANCE = new TeamService();
    private static final int TEAM_LIMIT = 3;
    private PlayerCache playerCache;
    private TeamCache teamCache;
    private LocationCache locationCache;
    private TeamInviteProcessor inviteProcessor;

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
     * Creates a new team with provided name and leader.
     *
     * @param teamName the name of the new team
     * @param leader the leader of the new team
     * @throws Exists if a team already exists with {@code teamName}
     * @throws PlayerAlreadyTeamed if {@code leader} is already in a team
     */
    public void createTeam(@NotNull String teamName, @NotNull UUID leader) throws Exists, PlayerAlreadyTeamed {
        // throw if a team already exists with this name
        if (getTeamModel(teamName) != null) {
            throw new Exists(teamName);
        }
        // throw if the leader is already in a team
        final var leaderTeam = getPlayerModel(leader).getTeam_name();
        if (leaderTeam != null) {
            throw new PlayerAlreadyTeamed(leaderTeam);
        }
        // create the team
        teamCache.createModel(teamName, sqlTeam -> {
            sqlTeam.setLeader(leader);
        });
        // set the team leader for the leader
        playerCache.updateModel(leader, sqlPlayer -> {
            sqlPlayer.setTeam_name(teamName);
        });
    }

    private @Nullable SQLTeam getTeamModel(@NotNull String teamName) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        return teamCache.getModel(teamName);
    }

    /**
     * Set the leader provided you check the team exists and the player is already on the team.
     * @param teamName name of the team that you are changing the leader of.
     * @param newLeader the player on the team that you want to set leader to.
     */
    public void setLeader(@NotNull String teamName, @NotNull Player newLeader) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (getTeamModel(teamName) == null) {
            throw new IllegalStateException("This is a critical error and should not happen. A team with this name does not exist.");
        }
        teamCache.updateModel(teamName, sqlTeam -> {
            sqlTeam.setLeader(newLeader.getUniqueId());
        });
    }

    /**
     * Gets the team home Location.
     *
     * This method assumes the Team exists.
     * @param teamName the name of the team we know exists.
     * @return the home location or null if there is not one.
     */
    public @Nullable Location getTeamHome(@NotNull String teamName) {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (locationCache == null) throw new IllegalStateException("locationCache not initialized");

        final SQLTeam team = getTeamModel(teamName);
        if (team == null) {
            return null;
        }
        final String homeLocationName = team.getHomeLocationName();
        if (homeLocationName == null) {
            return null;
        }
        final SQLLocation sqlLocation = locationCache.getModel(homeLocationName);
        if (sqlLocation == null) {
            return null;
        }
        return sqlLocation.getLocation();
    }

    public void setTeamHome(@NotNull String teamName, @NotNull Location location) throws DoesNotExist {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (locationCache == null) throw new IllegalStateException("locationCache not initialized");

        // check if team exists
        final SQLTeam team = getTeamModel(teamName);
        if (team == null) {
            throw new DoesNotExist(teamName);
        }

        final String locationKey = teamName + "_home";
        // attempt to create location
        if (locationCache.getModel(locationKey) == null) {
            locationCache.createModel(locationKey, sqlLocation -> sqlLocation.setLocation(location));
        } else {
            locationCache.updateModel(locationKey, sqlLocation -> sqlLocation.setLocation(location));
        }

        // set home location in team model
        teamCache.updateModel(teamName, sqlTeam -> {
            sqlTeam.setHomeLocationName(locationKey);
        });
    }

    /**
     * Adds a player to a team.
     *
     * @param player the player to add
     * @throws DoesNotExist if a team with {@code teamName} does not exist
     * @throws Full if the team is full
     * @throws PlayerAlreadyTeamed if {@code player} is already in a team
     */
    public void joinTeam(@NotNull String teamName, @NotNull UUID player) throws DoesNotExist, Full, PlayerAlreadyTeamed {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");
        if (playerCache == null) throw new IllegalStateException("playerCache not initialized");

        // check if team exists
        final SQLTeam team = getTeamModel(teamName);
        if (team == null) {
            throw new DoesNotExist(teamName);
        }

        // check if team is full
        if (isTeamFull(teamName)) {
            throw new Full();
        }

        // check if player is already in a team
        final String playerTeam = getPlayerModel(player).getTeam_name();
        if (playerTeam != null) {
            throw new PlayerAlreadyTeamed(playerTeam);
        }

        // add player to team in db
        playerCache.updateModel(player, sqlPlayer -> {
            sqlPlayer.setTeam_name(teamName);
        });
    }


    public TeamInvite invitePlayer(@NotNull Player target, @NotNull String teamName) {
        // create invite
        final TeamInvite invite = new TeamInvite(target, teamName);
        inviteProcessor.setInvite(target, invite);
        return invite;
    }

    /**
     * Removes a player from their team.
     * <p>
     * This method removes the player from the team and removes the team if
     * removing the player leaves the team with no members. It also reassigns
     * the team's leader if {@code player} is the current leader.
     *
     * @param player the player to remove
     * @throws PlayerNotTeamed if the player is not in a team
     */
    public void leaveTeam(@NotNull UUID player) throws PlayerNotTeamed {
        if (teamCache == null) throw new IllegalStateException("teamCache not initialized");

        // get the player's team name
        final String teamName = getTeam(player);
        if (teamName == null) {
            // they have no team
            throw new PlayerNotTeamed();
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
            // DELETE THE TEAM
            teamCache.deleteModel(teamName);
            // DELETE HOME LOCATION OF TEAM
            locationCache.deleteModel(teamName + "_home");
        } else {
            // check if the player leaving is the current leader of the team
            if (teamCache.getModel(teamName).getLeader().equals(player)) {
                // give team leader to someone else
                teamCache.updateModel(teamName, sqlTeam -> {
                    sqlTeam.setLeader(members.get(0));
                });
            }
        }
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

    public TeamInvite getInvite(@NotNull Player player) {
        return inviteProcessor.getInvite(player);
    }

    public boolean isTeamFull(@NotNull String teamName) {
        return getTeamMembers(teamName).size() == TEAM_LIMIT;
    }

    // call this after SQLService is initialized
    public void setup() {
        // capture cache instances
        playerCache = SQLService.getInstance().getPlayerCache();
        teamCache = SQLService.getInstance().getTeamCache();
        locationCache = SQLService.getInstance().getLocationCache();
        // set up processor
        if (inviteProcessor != null && !inviteProcessor.isCancelled()) {
            inviteProcessor.cancel();
        }
        inviteProcessor = new TeamInviteProcessor();
    }

    public static TeamService getInstance() {
        return INSTANCE;
    }
}
