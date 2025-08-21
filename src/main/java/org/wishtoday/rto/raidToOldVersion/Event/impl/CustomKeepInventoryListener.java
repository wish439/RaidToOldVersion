package org.wishtoday.rto.raidToOldVersion.Event.impl;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.wishtoday.rto.raidToOldVersion.Command.SetKeepInventory;
import org.wishtoday.rto.raidToOldVersion.Config.Config;

public class CustomKeepInventoryListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        setEvents(e, SetKeepInventory.getKeepInventoryState(e.getPlayer()));
    }

    private void setEvents(PlayerDeathEvent e, boolean b) {
        Player player = e.getPlayer();
        Boolean keepInventory = player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY);
        if (Boolean.TRUE.equals(keepInventory)) return;
        if (Config.isKeep_inventory_toggle()) return;
        e.setKeepInventory(b);
        if (b) e.getDrops().clear();
        e.setKeepLevel(b);
        e.setShouldDropExperience(!b);
    }
}
