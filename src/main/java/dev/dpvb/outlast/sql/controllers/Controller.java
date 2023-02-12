package dev.dpvb.outlast.sql.controllers;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;

/**
 * @param <PK> the primary key type
 * @param <M> the model
 */
public abstract class Controller<PK, M> {
    protected final Connection connection;

    protected Controller(Connection connection) {
        this.connection = connection;
    }

    public abstract @Nullable M getModel(PK key);

    public abstract List<M> getModels();

    public abstract void updateModel(M model);

    public abstract void insertModel(PK key);
}
