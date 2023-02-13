package dev.dpvb.outlast.sql.cache;

import dev.dpvb.outlast.sql.controllers.TeamController;
import dev.dpvb.outlast.sql.models.SQLTeam;

public class TeamCache extends Cache<TeamController, String, SQLTeam> {
    public TeamCache(TeamController controller) {
        super(controller);
    }

    @Override
    protected String extractKey(SQLTeam model) {
        return model.getName();
    }

    @Override
    protected SQLTeam createNewModel(String key) {
        return new SQLTeam(key);
    }

    @Override
    protected String getType() {
        return "Team";
    }
}
