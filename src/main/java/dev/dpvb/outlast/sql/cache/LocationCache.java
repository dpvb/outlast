package dev.dpvb.outlast.sql.cache;

import dev.dpvb.outlast.sql.controllers.LocationController;
import dev.dpvb.outlast.sql.models.SQLLocation;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

public class LocationCache extends Cache<LocationController, String, SQLLocation>{

    public LocationCache(LocationController controller) {
        super(controller);
    }

    @Override
    protected String extractKey(SQLLocation model) {
        return model.getLoc_name();
    }

    @Override
    protected SQLLocation createNewModel(String key) {
        return new SQLLocation(key);
    }

    @Override
    protected String getType() {
        return "Location";
    }

    /**
     * @deprecated SQLLocations require further initialization.
     * @param key a location name
     */
    @Deprecated
    @Contract("_ -> fail")
    @Override
    public void createModel(String key) {
        throw new UnsupportedOperationException("Use createModel(String, Consumer<SQLLocation>) instead.");
    }

    /**
     * @param key a location name
     * @param consumer a consumer that initializes the location
     * @see SQLLocation#SQLLocation(String)
     */
    @Override
    public void createModel(String key, Consumer<SQLLocation> consumer) {
        super.createModel(key, consumer);
    }
}
