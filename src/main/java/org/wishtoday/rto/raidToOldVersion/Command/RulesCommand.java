package org.wishtoday.rto.raidToOldVersion.Command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.wishtoday.rto.raidToOldVersion.Config.Config;

@SuppressWarnings("UnstableApiUsage")
public class RulesCommand{


    public static void registerCommand(Commands commands) {
        commands.register(Commands.literal("rules")
                .executes(RulesCommand::getRules)
                .build());
    }

    private static int getRules(CommandContext<CommandSourceStack> context) {
        ConfigurationSection texts = Config.getRules_text();
        CommandSender sender = context.getSource().getSender();
        if (texts == null) {
            sender.sendMessage("rule为空,请在配置文件中更改");
            return 0;
        }
        texts.getValues(true)
                .values()
                .stream()
                .filter(object -> object instanceof String)
                .map((object -> (String) object))
                .forEach(sender::sendMessage);
        return 1;
    }
}