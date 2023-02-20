package dev.dpvb.outlast.sql.controllers;

import dev.dpvb.outlast.sql.models.SQLPlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Interacts with the database to retrieve player stat models.
 */
public class PlayerController extends Controller<UUID, SQLPlayer> {

    public static final String TABLE = "player";
    public static final String PK = "player_uuid";

    public PlayerController(Connection connection) {
        super(connection);
    }

    /**
     * Retrieves information for a given player from the database.
     * <p>
     * Returns null if data for the player does not exist in the database.
     *
     * @param uuid the UniqueID of the player
     * @return a player stat model for {@code uuid} or null
     */
    @Override
    public @Nullable SQLPlayer getModel(UUID uuid) {
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE " + PK + " = UUID_TO_BIN(?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                final SQLPlayer sqlPlayer = new SQLPlayer(uuid);
                sqlPlayer.setKills(rs.getShort("kills"));
                sqlPlayer.setDeaths(rs.getShort("deaths"));
                sqlPlayer.setCoins(rs.getInt("coins"));
                sqlPlayer.setAttack_damage(rs.getByte("attack_damage"));
                sqlPlayer.setFirst_join_time(rs.getTimestamp("first_join_time"));
                sqlPlayer.setLast_join_time(rs.getTimestamp("last_join_time"));
                sqlPlayer.setTeam_name(rs.getString("team_name"));
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
    @Override
    public List<SQLPlayer> getModels() {
        final List<SQLPlayer> players = new ArrayList<>();
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT BIN_TO_UUID(player_uuid) as player_uuid, kills, deaths, coins, attack_damage, first_join_time, last_join_time, team_name FROM " + TABLE);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                final SQLPlayer sqlPlayer = new SQLPlayer(uuid);
                sqlPlayer.setKills(rs.getShort("kills"));
                sqlPlayer.setDeaths(rs.getShort("deaths"));
                sqlPlayer.setCoins(rs.getInt("coins"));
                sqlPlayer.setAttack_damage(rs.getByte("attack_damage"));
                sqlPlayer.setFirst_join_time(rs.getTimestamp("first_join_time"));
                sqlPlayer.setLast_join_time(rs.getTimestamp("last_join_time"));
                sqlPlayer.setTeam_name(rs.getString("team_name"));
                players.add(sqlPlayer);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getPlayers failed.");
            throw new RuntimeException(e);
        }

        return players;
    }

    @Override
    public void updateModel(SQLPlayer sqlPlayer) {
        try {
            final PreparedStatement ps = connection.prepareStatement("UPDATE " + TABLE + " SET kills = ?, deaths = ?, coins = ?, attack_damage = ?, first_join_time = ?, last_join_time = ?, team_name = ? WHERE player_uuid = UUID_TO_BIN(?)");
            ps.setShort(1, sqlPlayer.getKills());
            ps.setShort(2, sqlPlayer.getDeaths());
            ps.setInt(3, sqlPlayer.getCoins());
            ps.setByte(4, sqlPlayer.getAttack_damage());
            ps.setTimestamp(5, new Timestamp(sqlPlayer.getFirst_join_time().getTime()));
            ps.setTimestamp(6, new Timestamp(sqlPlayer.getLast_join_time().getTime()));
            ps.setString(7, sqlPlayer.getTeam_name());
            ps.setString(8, sqlPlayer.getPlayer_uuid().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("updatePlayer failed.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertModel(SQLPlayer sqlPlayer) {
        try {
            final PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE + " VALUES (UUID_TO_BIN(?), ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, sqlPlayer.getPlayer_uuid().toString());
            ps.setShort(2, sqlPlayer.getKills());
            ps.setShort(3, sqlPlayer.getDeaths());
            ps.setInt(4, sqlPlayer.getCoins());
            ps.setByte(5, sqlPlayer.getAttack_damage());
            ps.setTimestamp(6, new Timestamp(sqlPlayer.getFirst_join_time().getTime()));
            ps.setTimestamp(7, new Timestamp(sqlPlayer.getLast_join_time().getTime()));
            ps.setString(8, sqlPlayer.getTeam_name());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("insertPlayer failed.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteModel(UUID uuid) {
        try {
            final PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TABLE + " WHERE " + PK + " = UUID_TO_BIN(?)");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("deleteLocation failed.");
            throw new RuntimeException(e);
        }
    }
}
