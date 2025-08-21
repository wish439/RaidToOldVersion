package org.wishtoday.rto.raidToOldVersion.Command;

import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;
import org.wishtoday.rto.raidToOldVersion.Util.QuickUtils;

@SuppressWarnings({"UnstableApiUsage","SpellCheckingInspection"})
public class QuickShulkerCommand {
    public static void registerQuickCommands(Commands commands) {
        RaidToOldVersion instance = RaidToOldVersion.getInstance();
        registerEnderChest(commands, instance);
        registerWorkBeach(commands, instance);
        registerSmithingTable(commands, instance);
        registerStonecutter(commands, instance);
    }
    private static void registerWorkBeach(Commands commands, RaidToOldVersion plugin) {
        commands.register(
                Commands.literal("quickworkbeach")
                        .executes(context -> {
                            QuickUtils.tryOpenWorkbench(plugin, (Player) context.getSource().getSender());
                            return 1;
                        }).build()
        );
    }
    private static void registerEnderChest(Commands commands, RaidToOldVersion plugin) {
        commands.register(
                Commands.literal("quickenderchest")
                        .executes(context -> {
                            QuickUtils.tryOpenEnderChest(plugin, (Player) context.getSource().getSender());
                            return 1;
                        }).build()
        );
    }
    private static void registerSmithingTable(Commands commands, RaidToOldVersion plugin) {
        commands.register(
                Commands.literal("quicksmithingtable")
                        .executes(context -> {
                            QuickUtils.tryOpenSmithingTable(plugin, (Player) context.getSource().getSender());
                            return 1;
                        }).build()
        );
    }
    private static void registerStonecutter(Commands commands, RaidToOldVersion plugin) {
        commands.register(
                Commands.literal("quickstonecutter")
                        .executes(context -> {
                            QuickUtils.tryOpenStonecutter(plugin, (Player) context.getSource().getSender());
                            return 1;
                        }).build()
        );
    }
}
