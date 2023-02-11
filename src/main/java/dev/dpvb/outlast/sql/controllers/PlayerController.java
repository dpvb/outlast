package dev.dpvb.outlast.sql.controllers;

import dev.dpvb.outlast.sql.models.SQLPlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Interacts with the database to retrieve player stat models.
 */
public class PlayerController {

    private final Connection connection;

    public PlayerController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves information for a given player from the database.
     * <p>
     * Returns null if data for the player does not exist in the database.
     *
     * @param uuid the UniqueID of the player
     * @return a player stat model for {@code uuid} or null
     */
    public @Nullable SQLPlayer getPlayer(UUID uuid) {
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM player WHERE player_uuid = UUID_TO_BIN(?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                final SQLPlayer sqlPlayer = new SQLPlayer(uuid);
                sqlPlayer.setKills(rs.getShort("kills"));
                sqlPlayer.setDeaths(rs.getShort("deaths"));
                sqlPlayer.setCoins(rs.getInt("coins"));
                sqlPlayer.setStrengthModifier(rs.getByte("strength_modifier"));
                sqlPlayer.setTeam(rs.getString("team_name"));
                return sqlPlayer;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getPlayer failed.");
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Retrieves information for all players from the database.
     *
     * @return a list of player stat models
     */
    public List<SQLPlayer> getPlayers() {
        final List<SQLPlayer> players = new ArrayList<>();
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM player");
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final UUID uuid = UUID.nameUUIDFromBytes(rs.getBytes("player_uuid"));
                final SQLPlayer sqlPlayer = new SQLPlayer(uuid);
                sqlPlayer.setKills(rs.getShort("kills"));
                sqlPlayer.setDeaths(rs.getShort("deaths"));
                sqlPlayer.setCoins(rs.getInt("coins"));
                sqlPlayer.setStrengthModifier(rs.getByte("strength_modifier"));
                sqlPlayer.setTeam(rs.getString("team_name"));
                players.add(sqlPlayer);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getPlayers failed.");
            throw new RuntimeException(e);
        }

        return players;
    }

}
