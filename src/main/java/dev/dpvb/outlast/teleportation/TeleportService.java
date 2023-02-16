package dev.dpvb.outlast.teleportation;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.models.SQLLocation;
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
     * This method returns null if the player is not in a team or if the
     * player's team does not currently have a home set.
     *
     * @param player the player to teleport
     * @return a channeling teleport or null
     */
    public @Nullable ChannelingTeleport teleportHome(@NotNull Player player) {
        if (teleportRunner == null) throw new IllegalStateException("Teleport runner not initialized");
        return new ChannelingTeleport.TeamHomeChannel(player);
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
        getRequests(target).add(request);
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
     * Gets the teleport requests for a player.
     *
     * @param player a player
     * @return the teleport requests for the player
     */
    public @NotNull Queue<TeleportRequest> getRequests(@NotNull Player player) {
        if (requestProcessor == null) throw new IllegalStateException("Request processor not initialized");
        return requestProcessor.getRequests(player);
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
