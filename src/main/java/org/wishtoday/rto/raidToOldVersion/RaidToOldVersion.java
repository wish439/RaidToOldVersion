package org.wishtoday.rto.raidToOldVersion;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.wishtoday.rto.raidToOldVersion.Event.RegisterEvent;

public final class RaidToOldVersion extends JavaPlugin implements Listener {
    private static RaidToOldVersion instance;
    private NamespacedKey shulkerInvKey;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        shulkerInvKey = new NamespacedKey(this, "shulker_inventory");

        RegisterEvent.register(this, shulkerInvKey);
        getServer().getPluginManager().registerEvents(this, this);
    }
    public static RaidToOldVersion getInstance() {
        return instance;
    }

    public NamespacedKey getShulkerInvKey() {
        return shulkerInvKey;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onTickStart(ServerTickStartEvent e) {
        this.getServer().getOnlinePlayers().forEach(player -> {
            player.setExpCooldown(0);
        });
    }
    public boolean isHandInteractEnabled() {
        return true;
    }

    public boolean isInventoryInteractEnabled() {
        return true;
    }
}