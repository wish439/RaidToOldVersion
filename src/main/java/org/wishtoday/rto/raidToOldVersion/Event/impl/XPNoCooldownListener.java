package org.wishtoday.rto.raidToOldVersion.Event.impl;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.wishtoday.rto.raidToOldVersion.Config.Config;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

public class XPNoCooldownListener implements Listener {
    @EventHandler
    public void onTickStart(ServerTickStartEvent e) {
        if (!Config.isXp_no_cooldown()) return;
        //noinspection CodeBlock2Expr
        RaidToOldVersion.getInstance().getServer().getOnlinePlayers().forEach(player -> {
            player.setExpCooldown(0);
        });
    }
}
