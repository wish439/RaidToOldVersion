package org.wishtoday.rto.raidToOldVersion.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.rto.raidToOldVersion.Config.Config;

public class RulesCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender
            , @NotNull Command cmd
            , @NotNull String label
            , String[] args) {
        if (sender instanceof Player player) {
            ConfigurationSection texts = Config.getRules_text();
            if (texts == null) return false;
            texts.getValues(true)
                    .values()
                    .stream()
                    .filter(object -> object instanceof String)
                    .map((object -> (String) object))
                    .forEach(player::sendMessage);
            return true;
        } else {
            sender.sendMessage("§c只有玩家可以使用此命令!");
            return false;
        }
    }
}