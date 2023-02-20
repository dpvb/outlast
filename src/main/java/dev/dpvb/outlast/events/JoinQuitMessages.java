package dev.dpvb.outlast.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitMessages implements Listener {

    @EventHandler
    public void onPlayerJoinMessage(PlayerJoinEvent event) {
        event.joinMessage(generateMessage("+", NamedTextColor.GREEN, event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerQuitMessage(PlayerQuitEvent event) {
        event.quitMessage(generateMessage("-", NamedTextColor.RED, event.getPlayer().getName()));
    }

    private static Component generateMessage(String symbol, TextColor color, String playerName) {
        return Component.text("[").applyFallbackStyle(Style.style(NamedTextColor.GRAY))
                .append(Component.text(symbol).color(color))
                .append(Component.text("] " + playerName));
    }

}
