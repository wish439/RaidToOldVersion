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
        //passTick = tick;
        passTick = tick - 1;
    }

    @SuppressWarnings("unused")
    public int getTick() {
        return tick;
    }

    public UUID getUuid() {
        return uuid;
    }

    @SuppressWarnings("unused")
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @SuppressWarnings("unused")
    public int getPassTick() {
        return passTick;
    }

    @SuppressWarnings("unused")
    public void setPassTick(int passTick) {
        this.passTick = passTick;
    }
    @SuppressWarnings("unused")
    public void addPassTick(int passTick) {
        this.passTick += passTick;
    }
    @SuppressWarnings("unused")
    public void addPassTick() {
        this.passTick++;
    }
    public void checkPassTick(Consumer<Player> r) {
        if (passTick <= 0) {
            passTick = tick - 1;
            //passTick = tick;
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            r.accept(player);
        }else {
            passTick--;
        }
    }
}
