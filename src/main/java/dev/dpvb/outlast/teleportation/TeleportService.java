package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.models.SQLLocation;
import dev.dpvb.outlast.teams.TeamService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

/**
 * Handles teleportation.
 */
public class TeleportService {
    private static final TeleportService INSTANCE = new TeleportService();
    private TeleportRunner teleportRunner;
    private TeleportRequestProcessor requestProcessor;

    /**
     * Teleports a player to the player's team's home.
     * <p>
     * This method returns null if the
     * player's team does not currently have a home set.
     *
     * @param player the player to teleport
     * @param teamName a team that we know exists.
     * @return a channeling teleport or null
     */
    public @Nullable ChannelingTeleport teleportHome(@NotNull Player player, @NotNull String teamName) {
        if (teleportRunner == null) throw new IllegalStateException("Teleport runner not initialized");

        final TeamService teamService = TeamService.getInstance();
        Location teamHome = teamService.getTeamHome(teamName);
        if (teamHome == null) {
            return null;
        }

        ChannelingTeleport teleport = new ChannelingTeleport.TeamHomeChannel(player, teamHome);
        teleportRunner.add(teleport);
        return teleport;
    }

    /**
     * Requests a teleport to another player.
     *
     * @param player the player who is requesting the teleport
     * @param target the request target
     * @return a teleport request
     */
    public @NotNull TeleportRequest requestTeleport(@NotNull Player player, @NotNull Player target) {
        final TeleportRequest request = new TeleportRequest(player, target);
        requestProcessor.setRequest(target, request);
        return request;
    }

    /**
     * Teleports a player to (game) spawn.
     * <p>
     * This method returns null if the location {@code spawn} is not set.
     *
     * @param player the player to teleport
     * @return a channeling teleport or null
     */
    public @Nullable ChannelingTeleport teleportSpawn(@NotNull Player player) {
        if (teleportRunner == null) throw new IllegalStateException("Teleport runner not initialized");
        final SQLLocation spawn = SQLService.getInstance().getLocationCache().getModel("spawn");
        if (spawn == null) {
            return null; // follows contract
        }

        ChannelingTeleport.LocationChannel channel = new ChannelingTeleport.LocationChannel(player, spawn.getLocation());
        teleportRunner.add(channel);
        return channel;
    }

    /**
     * Use ChannelingTeleport to Teleport a Player to another Player.
     * @param player the player teleporting
     * @param target the destination player
     * @return ChannelingTeleport
     */
    public ChannelingTeleport teleportPlayer(@NotNull Player player, @NotNull Player target) {
        if (teleportRunner == null) throw new IllegalStateException("Teleport runner not initialized");
        ChannelingTeleport.PlayerChannel channel = new ChannelingTeleport.PlayerChannel(player, target);
        teleportRunner.add(channel);
        return channel;
    }

    /**
     * Gets the TeleportRequest for a player.
     *
     * @param player a player
     * @return the teleport requests for the player or null
     */
    public TeleportRequest getRequest(@NotNull Player player) {
        if (requestProcessor == null) throw new IllegalStateException("Request processor not initialized");
        return requestProcessor.getRequest(player);
    }

    public void setupRunner() {
        if (teleportRunner != null && !teleportRunner.isCancelled()) {
            teleportRunner.cancel();
        }
        teleportRunner = new TeleportRunner();
    }

    public void setupRequestProcessor() {
        if (requestProcessor != null && !requestProcessor.isCancelled()) {
            requestProcessor.cancel();
        }
        requestProcessor = new TeleportRequestProcessor();
    }

    public static TeleportService getInstance() {
        return INSTANCE;
    }
}
