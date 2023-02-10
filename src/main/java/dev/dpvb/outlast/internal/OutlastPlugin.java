package dev.dpvb.outlast.internal;

import dev.dpvb.outlast.api.OutlastAPI;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

public class OutlastPlugin extends JavaPlugin implements OutlastAPI {
    private final HashMap<Class<?>, Object> services = new HashMap<>();

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

    @Override
    public void withPlugin(@NotNull Consumer<JavaPlugin> action) {
        action.accept(this);
    }

    @Override
    public <T> void withServiceGraceful(@NotNull Class<T> service, @NotNull Consumer<T> action) {
        synchronized (services) {
            final var instance = services.get(service);
            if (instance != null) {
                action.accept(service.cast(instance));
            }
        }
    }

    @Override
    public <T> void withService(@NotNull Class<T> service, @NotNull Consumer<T> action) {
        synchronized (services) {
            final var instance = services.get(service);
            if (instance == null) {
                throw new IllegalStateException("Service " + service.getName() + " is not available");
            }
            action.accept(service.cast(instance));
        }
    }

    public <T> void putService(Class<T> service, T instance) {
        synchronized (services) {
            services.put(service, instance);
        }
    }
}
