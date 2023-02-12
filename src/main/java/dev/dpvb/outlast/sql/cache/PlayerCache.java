package dev.dpvb.outlast.sql.cache;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.controllers.PlayerController;
import dev.dpvb.outlast.sql.models.SQLPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerCache {

    private final Map<UUID, SQLPlayer> playerMap = new HashMap<>();
    private final PlayerController playerController;

    public PlayerCache(PlayerController playerController) {
        this.playerController = playerController;
    }

    public void load() {
        final List<SQLPlayer> sqlPlayers = playerController.getPlayers();
        for (SQLPlayer sqlPlayer : sqlPlayers) {
            playerMap.put(sqlPlayer.getUuid(), sqlPlayer);
        }
    }

    public void updateSQLPlayer(UUID uuid, Consumer<SQLPlayer> consumer) {
        final SQLPlayer sqlPlayer = playerMap.get(uuid);
        if (sqlPlayer == null) {
            throw new IllegalStateException("Couldn't find a SQLPlayer with this UUID though it should be cached.");
        }

        consumer.accept(sqlPlayer);
    }

    public void createSQLPlayer(UUID uuid) {
        playerMap.put(uuid, new SQLPlayer(uuid));
    }

}
