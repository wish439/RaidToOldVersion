package org.wishtoday.rto.raidToOldVersion.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.rto.raidToOldVersion.Util.PlayerAttacks;

import java.util.UUID;

import static org.wishtoday.rto.raidToOldVersion.RaidToOldVersion.PlayerAndTicks;

public class TickAttack implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender
            , @NotNull Command command
            , @NotNull String label, @NotNull String[] args) {
        if (!(label.equalsIgnoreCase("tickattack")
                || label.equalsIgnoreCase("tk")
                || label.equalsIgnoreCase("tickak"))) return false;
        if (!(sender instanceof Player player)) return false;
        if (args.length != 1) {
            if (args.length != 0) return false;
            UUID id = player.getUniqueId();
            //if (!hasUUIDFromList(id)) return false;
            removeFromList(id, player);
            return true;
        }
        String arg = args[0];
        int i = -1;
        try {
            i = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        if (i == -1) return false;
        UUID uuid = player.getUniqueId();
        addToList(i, uuid, player);
        return true;
    }

    public void addToList(int i, UUID uuid, Player player) {
        PlayerAndTicks.removeIf(playerAttacks -> playerAttacks.getUuid().equals(uuid));
        PlayerAndTicks.add(new PlayerAttacks(i, uuid));
        player.sendMessage("你现在每隔" + i + "tick后会攻击");
    }

    public void removeFromList(UUID uuid, Player player) {
        PlayerAndTicks.removeIf(playerAttacks -> playerAttacks.getUuid().equals(uuid));
        player.sendMessage("你现在不会攻击了");
    }

    public boolean hasUUIDFromList(UUID uuid) {
        for (PlayerAttacks playerAndTick : PlayerAndTicks) {
            if (playerAndTick.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}
