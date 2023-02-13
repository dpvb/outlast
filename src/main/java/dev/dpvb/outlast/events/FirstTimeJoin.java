package dev.dpvb.outlast.events;

import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstTimeJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if they have a Player Info model!
        final PlayerCache cache = SQLService.getInstance().getPlayerCache();
        final Player player = event.getPlayer();
        if (cache.getModel(player.getUniqueId()) == null) {
            // Player does not exist in DB
            cache.createModel(player.getUniqueId());
            Bukkit.getLogger().info("Player entry added for UUID: " + player.getUniqueId());
        }
    }

}
