package org.wishtoday.rto.raidToOldVersion.Event.impl;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.wishtoday.rto.raidToOldVersion.Util.PlayerAttacks;

import static org.wishtoday.rto.raidToOldVersion.RaidToOldVersion.PlayerAndTicks;

public class AttackListener implements Listener {
    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {
        for (PlayerAttacks playerAndTick : PlayerAndTicks) {
            playerAndTick.checkPassTick(player -> {
                RayTraceResult result = searchEntity(player, 4.5);
                //RayTraceResult result = player.rayTraceEntities(3);
                if (result == null) return;
                Entity entity = result.getHitEntity();
                if (entity == null) return;
                player.attack(entity);
                player.swingMainHand();
            });
        }
    }
    @SuppressWarnings("SameParameterValue")
    private RayTraceResult searchEntity(Player player, double maxSqDist) {
        Location location = player.getEyeLocation();
        Vector multiply = location.getDirection().multiply(4.5f);
        return player.getWorld().rayTraceEntities(location, multiply, maxSqDist, e -> e.getUniqueId() != player.getUniqueId());
    }
}
