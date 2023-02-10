package dev.dpvb.outlast.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface OutlastAPI {
    /**
     * Performs an action with the Outlast plugin instance.
     *
     * @param action the action to perform
     */
    void withPlugin(@NotNull Consumer<JavaPlugin> action);

    /**
     * Performs an action with the requested service if possible.
     * <p>
     * If a service instance is not present this method will do nothing.
     *
     * @param service a service class
     * @param action the action to perform
     * @param <T> the type of the service
     */
    <T> void withServiceGraceful(@NotNull Class<T> service, @NotNull Consumer<T> action);

    /**
     * Performs an action with the requested service.
     * <p>
     * If a service instance is not present this method will throw.
     *
     * @param service a service class
     * @param action the action to perform
     * @param <T> the type of the service
     * @throws IllegalStateException if a service instance is not available
     */
    <T> void withService(@NotNull Class<T> service, @NotNull Consumer<T> action);

    /**
     * Gets the API instance.
     *
     * @return the Outlast API instance
     * @throws IllegalStateException if the plugin is not loaded
     */
    static @NotNull OutlastAPI getInstance() throws IllegalStateException {
        final var api = Bukkit.getServicesManager().load(OutlastAPI.class);
        if (api == null) {
            throw new IllegalStateException("OutlastAPI is not initialized");
        }
        return api;
    }
}
