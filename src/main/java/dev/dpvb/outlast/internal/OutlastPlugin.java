package dev.dpvb.outlast.internal;

import dev.dpvb.outlast.api.OutlastAPI;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class OutlastPlugin extends JavaPlugin implements OutlastAPI {
    @Override
    public void onEnable() {
        // Register the API
        getServer().getServicesManager().register(OutlastAPI.class, this, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        // Unregister the API
        getServer().getServicesManager().unregister(OutlastAPI.class, this);
    }
}
