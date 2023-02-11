package dev.dpvb.outlast.sql;

import dev.dpvb.outlast.internal.OutlastPlugin;
import dev.dpvb.outlast.sql.controllers.PlayerController;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLService {

    private static final SQLService INSTANCE = new SQLService();
    private boolean started = false;
    private Connection connection;

    private PlayerController playerController;

    /**
     * Initialize a connection to the MySQL Database
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

        started = true;
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            Bukkit.getLogger().info("Disconnected from Database");
            connection.close();
        }
    }

    private String getConnectionString() {
        return OutlastPlugin.Configuration.getMySQLConnString();
    }

    public static SQLService getInstance() {
        return INSTANCE;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }
}
