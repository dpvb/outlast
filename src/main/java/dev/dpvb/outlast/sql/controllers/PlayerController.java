package dev.dpvb.outlast.sql.controllers;

import dev.dpvb.outlast.sql.models.SQLPlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerController {

    private final Connection connection;

    public PlayerController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets a Player's information and creates a {@link SQLPlayer} if stats with this Player's UUID exists.
     * @param uuid The uuid of the Player's stats you want to retrieve.
     * @return {@link SQLPlayer} or null if the Player does not exist.
     */
    public @Nullable SQLPlayer getPlayer(UUID uuid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM player WHERE player_uuid = UUID_TO_BIN(?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                SQLPlayer sqlPlayer = new SQLPlayer(uuid);
                sqlPlayer.setKills(rs.getShort("kills"));
                sqlPlayer.setDeaths(rs.getShort("deaths"));
                sqlPlayer.setCoins(rs.getInt("coins"));
                sqlPlayer.setTeam(rs.getString("team_name"));
                return sqlPlayer;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getPlayer failed.");
            throw new RuntimeException(e);
        }

        return null;
    }

}
