package dev.dpvb.outlast.teams;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class Team {
    private final @NotNull String name;
    private @NotNull UUID leader;

    public Team(@NotNull String name, @NotNull UUID leader) {
        this.name = name;
        this.leader = leader;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull UUID getLeader() {
        return leader;
    }

    public void setLeader(@NotNull UUID leader) {
        this.leader = leader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        if (!name.equals(team.name)) return false;
        return leader.equals(team.leader);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + leader.hashCode();
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
