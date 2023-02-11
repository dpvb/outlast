package dev.dpvb.outlast.internal;

import dev.dpvb.outlast.api.OutlastAPI;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class OutlastPlugin extends JavaPlugin {
    final OutlastAPIImpl api = new OutlastAPIImpl();

    @Override
    public void onEnable() {
        // Register the API
        getServer().getServicesManager().register(OutlastAPI.class, api, this, ServicePriority.Normal);
        // Setup Commands
        setupCommands();
    }

    @Override
    public void onDisable() {
        // Unregister the API
        getServer().getServicesManager().unregister(OutlastAPI.class, api);
    }

    private void setupCommands() {
        final Commands commands;
        try {
            commands = new Commands();
        } catch (final Exception e) {
            this.getLogger().severe("Failed to initialize the command manager.");
            throw new IllegalStateException(e);
        }
        commands.initCommands();
    }
}
