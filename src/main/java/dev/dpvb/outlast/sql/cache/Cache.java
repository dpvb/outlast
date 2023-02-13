package dev.dpvb.outlast.sql.cache;

import dev.dpvb.outlast.sql.controllers.Controller;

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

    public void load() {
        final List<V> models = controller.getModels();
        for (V model : models) {
            modelMap.put(extractKey(model), model);
        }
    }

    public void updateModel(K key, Consumer<V> consumer) {
        final V model = modelMap.get(key);
        if (model == null) {
            throw new IllegalStateException("Couldn't find a model with this key though it should be cached.");
        }
        consumer.accept(model);
    }

    public void createModel(K key) {
        modelMap.put(key, createNewModel(key));
    }

    public V deleteModel(K key) {
        final V remove = modelMap.remove(key);
        if (remove != null) {
            controller.deleteModel(key);
        }
        return remove;
    }
}
