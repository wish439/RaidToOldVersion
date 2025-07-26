package org.wishtoday.rto.raidToOldVersion.Event.impl;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.Iterator;

public class PlayerExpEvent implements Listener{
    @EventHandler
    public void onPlayerExpCooldownChange(PlayerPickupExperienceEvent event) {
        ExperienceOrb orb = event.getExperienceOrb();
        World world = orb.getWorld();
        Location location = orb.getLocation();
        Collection<ExperienceOrb> entities = world.getNearbyEntitiesByType(ExperienceOrb.class, location, 3);
        entities.remove(orb);
        Iterator<ExperienceOrb> iterator = entities.iterator();
        while (iterator.hasNext()) {
            ExperienceOrb entity = iterator.next();
            orb.setExperience(entity.getExperience() + orb.getExperience());
            entity.remove();
        }
    }
}
