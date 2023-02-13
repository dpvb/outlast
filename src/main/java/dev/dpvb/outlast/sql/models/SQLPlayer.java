package dev.dpvb.outlast.sql.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public final class SQLPlayer {
    private final UUID uuid;
    private short kills = 0;
    private short deaths = 0;
    private int coins = 0;
    private byte strengthModifier = 0;
    private Date first_join_time = Date.from(Instant.now());
    private Date last_join_time = Date.from(Instant.now());
    private @Nullable String team;

    public SQLPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public short getKills() {
        return kills;
    }

    public void setKills(short kills) {
        this.kills = kills;
    }

    public short getDeaths() {
        return deaths;
    }

    public void setDeaths(short deaths) {
        this.deaths = deaths;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public byte getStrengthModifier() {
        return strengthModifier;
    }

    public void setStrengthModifier(byte strengthModifier) {
        this.strengthModifier = strengthModifier;
    }

    public Date getFirst_join_time() {
        return first_join_time;
    }

    public void setFirst_join_time(Date first_join_time) {
        this.first_join_time = first_join_time;
    }

    public Date getLast_join_time() {
        return last_join_time;
    }

    public void setLast_join_time(Date last_join_time) {
        this.last_join_time = last_join_time;
    }

    public @Nullable String getTeam() {
        return team;
    }

    public void setTeam(@Nullable String team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLPlayer sqlPlayer = (SQLPlayer) o;

        if (kills != sqlPlayer.kills) return false;
        if (deaths != sqlPlayer.deaths) return false;
        if (coins != sqlPlayer.coins) return false;
        if (!uuid.equals(sqlPlayer.uuid)) return false;
        if (strengthModifier != sqlPlayer.strengthModifier) return false;
        return Objects.equals(team, sqlPlayer.team);
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + (int) kills;
        result = 31 * result + (int) deaths;
        result = 31 * result + coins;
        result = 31 * result + (int) strengthModifier;
        result = 31 * result + (team != null ? team.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SQLPlayer{" +
                "uuid=" + uuid +
                ", kills=" + kills +
                ", deaths=" + deaths +
                ", coins=" + coins +
                ", strengthModifier=" + strengthModifier +
                ", team='" + team + '\'' +
                '}';
    }
}
