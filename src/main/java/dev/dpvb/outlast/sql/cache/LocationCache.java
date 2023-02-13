package dev.dpvb.outlast.sql.cache;

import dev.dpvb.outlast.sql.controllers.LocationController;
import dev.dpvb.outlast.sql.models.SQLLocation;

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
}
