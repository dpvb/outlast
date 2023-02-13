package dev.dpvb.outlast.sql;

import dev.dpvb.outlast.internal.OutlastPlugin;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.controllers.LocationController;
import dev.dpvb.outlast.sql.controllers.PlayerController;
import dev.dpvb.outlast.sql.controllers.TeamController;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLService {

    private static final SQLService INSTANCE = new SQLService();
    private boolean started = false;
    private Connection connection;

    private PlayerController playerController;
    private LocationController locationController;
    private TeamController teamController;
    private PlayerCache playerCache;

    /**
     * Opens a connection to the MySQL Database and initializes controllers.
     */
    public void start() throws SQLException {
        if (started) {
            return;
        }

        // Initialize connection
        connection = DriverManager.getConnection(getConnectionString());
        Bukkit.getLogger().info("Connected to Database");

        // Create Controllers
        playerController = new PlayerController(connection);
        locationController = new LocationController(connection);
        teamController = new TeamController(connection);

        // Create Cache
        playerCache = new PlayerCache(playerController);
        playerCache.load();

        started = true;
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            Bukkit.getLogger().info("Disconnected from Database");
            connection.close();
            started = false;
        }
    }

    private String getConnectionString() {
        return OutlastPlugin.Configuration.getMySQLConnString();
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public LocationController getLocationController() {
        return locationController;
    }

    public TeamController getTeamController() {
        return teamController;
    }

    public static SQLService getInstance() {
        return INSTANCE;
    }
}
