package dev.dpvb.outlast.sql;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class SQLLocation {

    private final @NotNull String loc_name;
    private @NotNull String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public SQLLocation(String loc_name, Location location) {
        this.loc_name = loc_name;
        this.world = location.getWorld().toString();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLLocation sqlLocation = (SQLLocation) o;

        if (!world.equals(sqlLocation.world)) return false;
        if (x != sqlLocation.x) return false;
        if (y != sqlLocation.y) return false;
        if (z != sqlLocation.z) return false;
        if (pitch != sqlLocation.pitch) return false;
        return yaw == sqlLocation.yaw;
    }

    @Override
    public int hashCode() {
        int result = loc_name.hashCode();
        result = 31 * result + world.hashCode();
        result = 31 * result + (int) x;
        result = 31 * result + (int) y;
        result = 31 * result + (int) z;
        result = 31 * result + (int) yaw;
        result = 31 * result + (int) pitch;
        return result;
    }

    @Override
    public String toString() {
        return "SQLLocation{" +
                "loc_name='" + loc_name + '\'' +
                ", world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
