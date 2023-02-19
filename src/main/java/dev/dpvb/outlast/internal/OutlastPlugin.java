package dev.dpvb.outlast.internal;

import dev.dpvb.outlast.events.FirstTimeJoin;
import dev.dpvb.outlast.events.JoinQuitMessages;
import dev.dpvb.outlast.messages.Messages;
import dev.dpvb.outlast.messages.MiniMessageService;
import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.teams.TeamService;
import dev.dpvb.outlast.teleportation.TeleportService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class OutlastPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Setup Configuration File
        setupConfigFile();
        // Load Messages
        loadMessages();
        // Setup Database
        setupDatabase();
        // Setup Commands
        setupCommands();
        // Setup Listeners
        setupListeners();
        // Setup Teleport Runner and Request Processor
        TeleportService.getInstance().setupRunner();
        TeleportService.getInstance().setupRequestProcessor();
        // Set up Team Service
        TeamService.getInstance().setup();
    }

    @Override
    public void onDisable() {
        // Close Database
        closeDatabase();
    }

    private void setupConfigFile() {
        saveDefaultConfig();
        Configuration.config = getConfig();
    }

    private void loadMessages() {
        Messages.initBundles();
        MiniMessageService.builderSetup(b -> {});
    }

    private void setupDatabase() {
        try {
            SQLService.getInstance().start();
        } catch (SQLException e) {
            this.getLogger().severe("Couldn't connect to MySQL Database.");
            throw new IllegalStateException(e);
        }
    }

    private void closeDatabase() {
        try {
            SQLService.getInstance().disconnect();
        } catch (SQLException e) {
            this.getLogger().severe("Couldn't disconnect from MySQL Database.");
            throw new IllegalStateException(e);
        }
    }

    private void setupCommands() {
        final Commands commands = new Commands();
        try {
            commands.initCommands(this);
        } catch (final Exception e) {
            this.getLogger().severe("Failed to initialize the command manager.");
            throw new IllegalStateException(e);
        }
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new FirstTimeJoin(), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitMessages(), this);
    }

    public static class Configuration {
        private static FileConfiguration config;

        public static String getMySQLConnString() {
            return config.getString("mysql-conn-string");
        }
    }
}
