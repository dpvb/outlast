package dev.dpvb.outlast.internal;

import org.bukkit.plugin.java.JavaPlugin;

public class OutlastPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Setup Commands
        setupCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
}
