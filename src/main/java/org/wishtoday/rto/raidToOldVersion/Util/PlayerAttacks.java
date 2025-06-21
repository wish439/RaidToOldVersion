package org.wishtoday.rto.raidToOldVersion.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerAttacks {
    private final int tick;
    private UUID uuid;
    private int passTick;
    public PlayerAttacks(int tick, UUID uuid) {
        this.tick = tick;
        this.uuid = uuid;
        passTick = tick - 1;
    }

    public int getTick() {
        return tick;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getPassTick() {
        return passTick;
    }

    public void setPassTick(int passTick) {
        this.passTick = passTick;
    }
    public void addPassTick(int passTick) {
        this.passTick += passTick;
    }
    public void addPassTick() {
        this.passTick++;
    }
    public void checkPassTick(Consumer<Player> r) {
        if (passTick <= 0) {
            passTick = tick - 1;
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            r.accept(player);
        }else {
            passTick--;
        }
    }
}
