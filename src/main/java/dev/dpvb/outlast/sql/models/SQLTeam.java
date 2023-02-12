package dev.dpvb.outlast.sql.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class SQLTeam {
    private final @NotNull String name;
    private UUID leader;
    private String homeLocationName;

    /**
     * Creates a new team persistence object.
     * <p>
     * <strong>{@link #setLeader(UUID)} must be called to completely initialize
     * this object properly.</strong>
     *
     * @param name a team name
     */
    public SQLTeam(@NotNull String name) {
        this.name = name;
    }

    // suitable for unmarshalling
    public SQLTeam(@NotNull String name, @NotNull UUID leader, @Nullable String homeLocationName) {
        this.name = name;
        this.leader = leader;
        this.homeLocationName = homeLocationName;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull UUID getLeader() {
        return Objects.requireNonNull(leader, "leader has not been set!");
    }

    public void setLeader(@NotNull UUID leader) {
        this.leader = leader;
    }

    public @Nullable String getHomeLocationName() {
        return homeLocationName;
    }

    public void setHomeLocationName(@Nullable String homeLocationName) {
        this.homeLocationName = homeLocationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLTeam team = (SQLTeam) o;

        if (!name.equals(team.name)) return false;
        return Objects.equals(leader, team.leader);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Objects.hashCode(leader);
        return result;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", leader=" + leader +
                '}';
    }
}
