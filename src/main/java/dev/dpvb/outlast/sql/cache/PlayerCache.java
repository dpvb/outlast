package dev.dpvb.outlast.sql.cache;

import dev.dpvb.outlast.sql.controllers.PlayerController;
import dev.dpvb.outlast.sql.models.SQLPlayer;

import java.util.UUID;

public class PlayerCache extends Cache<PlayerController, UUID, SQLPlayer> {

    public PlayerCache(PlayerController playerController) {
        super(playerController);
    }

    @Override
    protected UUID extractKey(SQLPlayer model) {
        return model.getUuid();
    }

    @Override
    protected SQLPlayer createNewModel(UUID key) {
        return new SQLPlayer(key);
    }

}
