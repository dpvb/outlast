package dev.dpvb.outlast.internal;

import dev.dpvb.outlast.api.OutlastAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

class OutlastAPIImpl implements OutlastAPI {
    final HashMap<Class<?>, Object> services = new HashMap<>();

    @Override
    public void withPlugin(@NotNull Consumer<JavaPlugin> action) {
        action.accept(JavaPlugin.getPlugin(OutlastPlugin.class));
    }

    @Override
    public <T> void withServiceGraceful(@NotNull Class<T> service, @NotNull Consumer<T> action) {
        if (service == getClass()) {
            action.accept(service.cast(this));
            return;
        }
        synchronized (services) {
            final var instance = services.get(service);
            if (instance != null) {
                action.accept(service.cast(instance));
            }
        }
    }

    @Override
    public <T> void withService(@NotNull Class<T> service, @NotNull Consumer<T> action) {
        if (service == getClass()) {
            action.accept(service.cast(this));
            return;
        }
        synchronized (services) {
            final var instance = services.get(service);
            if (instance == null) {
                throw new IllegalStateException("Service " + service.getName() + " is not available");
            }
            action.accept(service.cast(instance));
        }
    }

    public <T> void putService(Class<? super T> service, T instance) {
        synchronized (services) {
            services.put(service, instance);
        }
    }
}
