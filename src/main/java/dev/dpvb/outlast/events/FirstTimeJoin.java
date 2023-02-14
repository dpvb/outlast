package dev.dpvb.outlast.events;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class FirstTimeJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if they have a Player Info model!
        final PlayerCache cache = SQLService.getInstance().getPlayerCache();
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (cache.getModel(uuid) == null) {
            // Player does not exist in DB, so create it.
            cache.createModel(uuid);
            Bukkit.getLogger().info("Player entry added for UUID: " + uuid);
        }

        cache.updateModel(uuid, sqlPlayer -> {
            sqlPlayer.setLast_join_time(Date.from(Instant.now()));
        });
    }

}