package dev.dpvb.outlast.sql;

import dev.dpvb.outlast.internal.OutlastPlugin;
import dev.dpvb.outlast.sql.cache.LocationCache;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.cache.TeamCache;
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

    private PlayerCache playerCache;
    private LocationCache locationCache;
    private TeamCache teamCache;

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

        // Create Cache
        playerCache = new PlayerCache(new PlayerController(connection));
        playerCache.load();
        teamCache = new TeamCache(new TeamController(connection));
        teamCache.load();
        locationCache = new LocationCache(new LocationController(connection));
        locationCache.load();

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

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public LocationCache getLocationCache() {
        return locationCache;
    }

    public TeamCache getTeamCache() {
        return teamCache;
    }

    public static SQLService getInstance() {
        return INSTANCE;
    }
}
