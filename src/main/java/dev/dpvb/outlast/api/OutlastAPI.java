package dev.dpvb.outlast.api;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public interface OutlastAPI {
    // as needed

    static @NotNull OutlastAPI getInstance() {
        final var api = Bukkit.getServicesManager().load(OutlastAPI.class);
        if (api == null) {
            throw new IllegalStateException("OutlastAPI is not initialized");
        }
        return api;
    }
}
