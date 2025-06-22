package org.wishtoday.rto.raidToOldVersion.Event.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.wishtoday.rto.raidToOldVersion.Command.SetKeepInventory;

public class CustomKeepInventoryListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        setEvents(e, SetKeepInventory.getKeepInventoryState(e.getPlayer()));
    }

    private void setEvents(PlayerDeathEvent e, boolean b) {
        e.setKeepInventory(b);
        if (b) e.getDrops().clear();
        e.setKeepLevel(b);
        e.setShouldDropExperience(!b);
    }
}
