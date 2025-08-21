package org.wishtoday.rto.raidToOldVersion.Command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.wishtoday.rto.raidToOldVersion.Config.Config;

@SuppressWarnings("UnstableApiUsage")
public class RTOCommand {
    public static void registerCommand(Commands commands) {
        commands.register(
                Commands.literal("rto")
                        .requires(source -> source.getSender().isOp())
                        .then(Commands.literal("reload").executes(RTOCommand::reloadConfig
                        )).build());
    }
    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        Config.reload();
        context.getSource().getSender().sendMessage("已重新加载");
        return 1;
    }
}
