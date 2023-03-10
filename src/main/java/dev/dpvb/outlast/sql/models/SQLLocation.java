package dev.dpvb.outlast.sql.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SQLLocation {

    private final @NotNull String loc_name;
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    /**
     * Creates a named location persistence object.
     * <p>
     * <strong>{@link #setLocation(Location)} must be called to completely
     * initialize this object properly.</strong>
     *
     * @param loc_name a name for the location
     */
    public SQLLocation(@NotNull String loc_name) {
        this.loc_name = loc_name;
    }

    /**
     * Creates a named location persistence object.
     * <p>
     * The supplied Bukkit location must have a world component.
     *
     * @param loc_name a name for the location
     * @param location a bukkit location
     * @throws NullPointerException if {@code location} does not have a world
     */
    public SQLLocation(@NotNull String loc_name, @NotNull Location location) {
        this.loc_name = loc_name;
        setLocation(location);
    }

    // for unmarshalling
    public SQLLocation(@NotNull String loc_name, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        this.loc_name = loc_name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public @NotNull String getLoc_name() {
        return loc_name;
    }

    public @NotNull String getWorld() {
        return Objects.requireNonNull(world, "world has not been set!");
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public @NotNull Location getLocation() {
        return new Location(Bukkit.getWorld(getWorld()), x, y, z, yaw, pitch);
    }

    public void setLocation(@NotNull Location location) {
        this.world = Objects.requireNonNull(location.getWorld(), "location must have a world").getName();
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

        if (!loc_name.equals(sqlLocation.loc_name)) return false;
        if (!Objects.equals(world, sqlLocation.world)) return false;
        if (x != sqlLocation.x) return false;
        if (y != sqlLocation.y) return false;
        if (z != sqlLocation.z) return false;
        if (pitch != sqlLocation.pitch) return false;
        return yaw == sqlLocation.yaw;
    }

    @Override
    public int hashCode() {
        int result = loc_name.hashCode();
        result = 31 * result + Objects.hashCode(world);
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
