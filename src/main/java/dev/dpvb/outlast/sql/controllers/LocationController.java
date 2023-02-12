package dev.dpvb.outlast.sql.controllers;

import dev.dpvb.outlast.sql.models.SQLLocation;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interacts with the database to retrieve location models.
 */
public class LocationController extends Controller<String, SQLLocation> {
    public static final String TABLE = "location";
    public static final String PK = "loc_name";

    public LocationController(Connection connection) {
        super(connection);
    }

    @Override
    public @Nullable SQLLocation getModel(String name) {
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE " + PK + " = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, name);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                return new SQLLocation(
                        name,
                        rs.getString("world"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                );
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getLocation failed.");
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<SQLLocation> getModels() {
        final List<SQLLocation> locations = new ArrayList<>();
        try {
            final PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                locations.add(new SQLLocation(
                        rs.getString(PK),
                        rs.getString("world"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                ));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("getLocations failed.");
            throw new RuntimeException(e);
        }
        return locations;
    }

    @Override
    public void updateModel(SQLLocation sqlLocation) {
        try {
            final PreparedStatement ps = connection.prepareStatement(
                    "UPDATE " + TABLE +
                            " SET world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?" +
                            " WHERE " + PK + " = ?"
            );
            ps.setString(1, sqlLocation.getWorld());
            ps.setDouble(2, sqlLocation.getX());
            ps.setDouble(3, sqlLocation.getY());
            ps.setDouble(4, sqlLocation.getZ());
            ps.setFloat(5, sqlLocation.getYaw());
            ps.setFloat(6, sqlLocation.getPitch());
            ps.setString(7, sqlLocation.getLoc_name());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("updateLocation failed.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertModel(SQLLocation sqlLocation) {
        try {
            final PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE + "(" + PK + ") VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, sqlLocation.getLoc_name());
            ps.setString(2, sqlLocation.getWorld());
            ps.setDouble(3, sqlLocation.getX());
            ps.setDouble(4, sqlLocation.getY());
            ps.setDouble(5, sqlLocation.getZ());
            ps.setFloat(6, sqlLocation.getYaw());
            ps.setFloat(7, sqlLocation.getPitch());
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("insertLocation failed.");
            throw new RuntimeException(e);
        }
    }

}
