package org.wishtoday.rto.raidToOldVersion.Event;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.wishtoday.rto.raidToOldVersion.Event.impl.*;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

public class RegisterEvent {
    public static void register(RaidToOldVersion plugin, NamespacedKey key) {
        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new AttackListener(), plugin);
        manager.registerEvents(new QuickListener(plugin,key), plugin);
        manager.registerEvents(new RaidToOldListener(), plugin);
        manager.registerEvents(new CustomKeepInventoryListener(), plugin);
        manager.registerEvents(new XPNoCooldownListener(), plugin);
    }
}
