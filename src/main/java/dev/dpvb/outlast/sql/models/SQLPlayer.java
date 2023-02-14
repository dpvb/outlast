package dev.dpvb.outlast.sql.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public final class SQLPlayer {
    private final UUID player_uuid;
    private short kills = 0;
    private short deaths = 0;
    private int coins = 0;
    private byte strength_modifier = 0;
    private @NotNull Date first_join_time = Date.from(Instant.now());
    private @NotNull Date last_join_time = Date.from(Instant.now());
    private @Nullable String team_name;

    public SQLPlayer(@NotNull UUID player_uuid) {
        this.player_uuid = player_uuid;
    }

    public @NotNull UUID getPlayer_uuid() {
        return player_uuid;
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

    public byte getStrength_modifier() {
        return strength_modifier;
    }

    public void setStrength_modifier(byte strength_modifier) {
        this.strength_modifier = strength_modifier;
    }

    public @NotNull Date getFirst_join_time() {
        return first_join_time;
    }

    public void setFirst_join_time(@NotNull Date first_join_time) {
        this.first_join_time = first_join_time;
    }

    public @NotNull Date getLast_join_time() {
        return last_join_time;
    }

    public void setLast_join_time(@NotNull Date last_join_time) {
        this.last_join_time = last_join_time;
    }

    public @Nullable String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(@Nullable String team_name) {
        this.team_name = team_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLPlayer sqlPlayer = (SQLPlayer) o;

        if (!player_uuid.equals(sqlPlayer.player_uuid)) return false;
        if (kills != sqlPlayer.kills) return false;
        if (deaths != sqlPlayer.deaths) return false;
        if (coins != sqlPlayer.coins) return false;
        if (strength_modifier != sqlPlayer.strength_modifier) return false;
        if (!first_join_time.equals(sqlPlayer.first_join_time)) return false;
        if (!last_join_time.equals(sqlPlayer.last_join_time)) return false;
        return Objects.equals(team_name, sqlPlayer.team_name);
    }

    @Override
    public int hashCode() {
        int result = player_uuid.hashCode();
        result = 31 * result + (int) kills;
        result = 31 * result + (int) deaths;
        result = 31 * result + coins;
        result = 31 * result + (int) strength_modifier;
        result = 31 * result + first_join_time.hashCode();
        result = 31 * result + last_join_time.hashCode();
        result = 31 * result + (team_name != null ? team_name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SQLPlayer{" +
                "player_uuid=" + player_uuid +
                ", kills=" + kills +
                ", deaths=" + deaths +
                ", coins=" + coins +
                ", strength_modifier=" + strength_modifier +
                ", first_join_time=" + first_join_time +
                ", last_join_time=" + last_join_time +
                ", team_name='" + team_name + '\'' +
                '}';
    }
}
