package org.wishtoday.rto.raidToOldVersion.Event.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.wishtoday.rto.raidToOldVersion.Network.CarpetHelloPacket;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new CarpetHelloPacket().sendHelloPacket(player);
    }
}
