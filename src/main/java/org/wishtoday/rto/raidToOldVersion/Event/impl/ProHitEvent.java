package org.wishtoday.rto.raidToOldVersion.Event.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.UUID;

public class ProHitEvent implements Listener {
    @EventHandler
    public void onProHit(ProjectileHitEvent e) {
        Projectile entity = e.getEntity();
        if (!(entity instanceof FishHook fishHook)) return;
        Entity hitEntity = e.getHitEntity();
        if (hitEntity == null) return;
        if (!(hitEntity instanceof LivingEntity livingEntity)) return;
        UUID id = fishHook.getOwnerUniqueId();
        if (id == null) return;
        Player player = Bukkit.getPlayer(id);
        if (player == null) return;
        livingEntity.knockback(0.3, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        livingEntity.damage(0);
    }
}
