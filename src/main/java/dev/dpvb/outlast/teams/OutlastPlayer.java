package dev.dpvb.outlast.teams;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class OutlastPlayer {
    private final UUID uuid;
    private short kills = 0;
    private short deaths = 0;
    private int coins = 0;
    private @Nullable String team;

    public OutlastPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
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

    public @Nullable String getTeam() {
        return team;
    }

    public void setTeam(@Nullable String team) {
        this.team = team;
    }
}
