package dev.dpvb.outlast.sql.cache;

import dev.dpvb.outlast.sql.controllers.Controller;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Cache<C extends Controller<K, V>, K, V> {
    protected final C controller;
    private final Map<K, V> modelMap = new HashMap<>();

    public Cache(C controller) {
        this.controller = controller;
    }

    /**
     * Extracts the primary key from a model.
     *
     * @param model a model object
     * @return the primary key
     */
    protected abstract K extractKey(V model);

    /**
     * Makes a new model object initialized with the provided key.
     *
     * @param key a primary key
     * @return a new model object
     */
    protected abstract V createNewModel(K key);

    protected abstract String getType();

    public void load() {
        final List<V> models = controller.getModels();
        for (V model : models) {
            modelMap.put(extractKey(model), model);
        }

        // FIXME use sendConsole with message
        Bukkit.getLogger().info("Loaded " + modelMap.keySet().size() + " of " + getType());
    }

    public V getModel(K key) {
        return modelMap.get(key);
    }

    public Collection<V> getModels() {
        return modelMap.values();
    }

    public void updateModel(K key, Consumer<V> consumer) {
        final V model = modelMap.get(key);
        if (model == null) {
            throw new IllegalStateException("Couldn't find a model with this key though it should be cached.");
        }
        consumer.accept(model);
        controller.updateModel(model);
    }

    public void createModel(K key) {
        createModel(key, v -> {});
    }

    public void createModel(K key, Consumer<V> consumer) {
        final V model = createNewModel(key);
        consumer.accept(model);
        modelMap.put(key, model);
        controller.insertModel(model);
    }

    public V deleteModel(K key) {
        final V remove = modelMap.remove(key);
        if (remove != null) {
            controller.deleteModel(key);
        }
        return remove;
    }
}
