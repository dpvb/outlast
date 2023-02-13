package dev.dpvb.outlast.sql.controllers;

import dev.dpvb.outlast.sql.models.SQLTeam;
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
 * Interacts with the database to retrieve team models.
 */
public class TeamController extends Controller<String, SQLTeam> {
    public static final String TABLE = "team";
    public static final String PK = "team_name";

    public TeamController(Connection connection) {
        super(connection);
    }

    @Override
    public @Nullable SQLTeam getModel(String name) {
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE " + PK + " = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, name);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                final SQLTeam sqlTeam = new SQLTeam(name);
                sqlTeam.setLeader(UUID.nameUUIDFromBytes(rs.getBytes("team_leader")));
                sqlTeam.setHomeLocationName(rs.getString("home_loc_name"));
                return sqlTeam;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getTeam failed.");
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<SQLTeam> getModels() {
        final List<SQLTeam> teams = new ArrayList<>();
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(new SQLTeam(
                        rs.getString(PK),
                        UUID.nameUUIDFromBytes(rs.getBytes("team_leader")),
                        rs.getString("home_loc_name")
                ));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getTeams failed.");
            throw new RuntimeException(e);
        }
        return teams;
    }

    @Override
    public void updateModel(SQLTeam sqlTeam) {
        try {
            final PreparedStatement ps = connection.prepareStatement(
                    "UPDATE " + TABLE +
                            " SET team_leader = UUID_TO_BIN(?), home_loc_name = ?" +
                            " WHERE " + PK + " = ?"
            );
            ps.setString(1, sqlTeam.getLeader().toString());
            ps.setString(2, sqlTeam.getHomeLocationName());
            ps.setString(3, sqlTeam.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("updateTeam failed.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertModel(SQLTeam sqlTeam) {
        try {
            final PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE + " VALUES (?, UUID_TO_BIN(?), ?)");
            ps.setString(1, sqlTeam.getName());
            ps.setString(2, sqlTeam.getLeader().toString());
            ps.setString(3, sqlTeam.getHomeLocationName());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("insertTeam failed.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteModel(String teamName) {
        try {
            final PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TABLE + " WHERE team_name = (?)");
            ps.setString(1, teamName);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("deleteLocation failed.");
            throw new RuntimeException(e);
        }
    }
}
