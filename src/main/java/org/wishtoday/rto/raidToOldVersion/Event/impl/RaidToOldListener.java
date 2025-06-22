package org.wishtoday.rto.raidToOldVersion.Event.impl;

import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RaidToOldListener implements Listener {
    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        PotionEffectType type = e.getModifiedType();
        EntityPotionEffectEvent.Cause cause = e.getCause();
        if (type == PotionEffectType.RAID_OMEN
                && cause.equals(EntityPotionEffectEvent.Cause.UNKNOWN)) {
            e.setCancelled(true);
            PotionEffect effect = player.getPotionEffect(PotionEffectType.BAD_OMEN);
            int i = 0;
            if (effect != null) i = effect.getAmplifier();
            //System.out.println(i);
            addToRaidOMen(player, i);
        }
    }
    private static void addToRaidOMen(Player player,int Amplifier) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RAID_OMEN, 1, Amplifier));
    }
    @EventHandler
    public void onPlayerKillEntity(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Raider raider)) return;
        if (!raider.isPatrolLeader()) return;
        if (raider.getRaid() != null) return;
        if (getRaidAt(raider.getLocation(),9216,raider.getWorld().getRaids()) != null) return;
        Player killer = raider.getKiller();
        if (killer == null) return;
        PotionEffect oldEffect = killer.getPotionEffect(PotionEffectType.BAD_OMEN);
        int i = -1;
        if (oldEffect != null) i = oldEffect.getAmplifier();
        i++;
        if (i >= 5) return;
        //System.out.println(oldEffect == null ? 0 : oldEffect.getAmplifier());
        killer.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN,
                        20 * 60 * 60 ,
                        i
                        //(oldEffect == null ? 0 : oldEffect.getAmplifier() + 1)
                        //0
                )
        );
        killer.playSound(killer, Sound.EVENT_MOB_EFFECT_BAD_OMEN,1,1);
    }
    /**
     * {@return the raid occurring within 96 block radius, or {@code null} if there is none}
     */
    @Nullable
    public Raid getRaidAt(Location pos, int searchDistance, List<Raid> raids) {
        Raid raid = null;
        double d = searchDistance;

        for (Raid raid2 : raids) {
            double e = raid2.getLocation().distanceSquared(pos);
            if (raid2.isStarted() && e < d) {
                raid = raid2;
                d = e;
            }
        }

        return raid;
    }
}
