package org.wishtoday.rto.raidToOldVersion.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.rto.raidToOldVersion.Config.Config;

public class RTOCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender
            , @NotNull Command command
            , @NotNull String label
            , @NotNull String[] args) {
        if (args.length == 0) return false;
        switch (args[0]) {
            case "reload" -> {
                Config.reload();
                return true;
            }
        }
        return true;
    }
}
