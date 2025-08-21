package org.wishtoday.rto.raidToOldVersion.Command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.rto.raidToOldVersion.Util.PlayerAttacks;

import java.util.UUID;

import static org.wishtoday.rto.raidToOldVersion.RaidToOldVersion.PlayerAndTicks;

@SuppressWarnings({"UnstableApiUsage","SpellCheckingInspection"})
public class TickAttack {
    @SuppressWarnings("CommentedOutCode")
    public static void registerCommand(Commands commands) {
        //LiteralCommandNode<CommandSourceStack> build = getTkCommand();
        commands.register(getTkCommand("tk"));
        /*commands.register(
                Commands.literal("tickattck").redirect(build).build()
        );*/
        commands.register(
                getTkCommand("tickattack")
        );
    }

    private static LiteralCommandNode<CommandSourceStack> getTkCommand(String name) {
        return Commands.literal(name)
                .executes(
                        context -> {
                            if (!(context.getSource().getSender() instanceof Player player)) return 0;
                            if (!hasUUIDFromList(player.getUniqueId())) return 0;
                            return
                                    removeFromList(player.getUniqueId(), player);
                        }
                )
                .then(
                        Commands.argument("attacktick", IntegerArgumentType.integer())
                                .executes(
                                        context -> {
                                            int i = IntegerArgumentType.getInteger(context, "attacktick");
                                            if (!(context.getSource().getSender() instanceof Player player)) return 0;
                                            UUID uuid = player.getUniqueId();
                                            return addToList(i, uuid, player);
                                        }
                                )
                ).build();
    }

    @SuppressWarnings("unused")
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
        int i;
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

    public static int addToList(int i, UUID uuid, Player player) {
        PlayerAndTicks.removeIf(playerAttacks -> playerAttacks.getUuid().equals(uuid));
        PlayerAndTicks.add(new PlayerAttacks(i, uuid));
        player.sendMessage("你现在每隔" + i + "tick后会攻击");
        return 1;
    }

    public static int removeFromList(UUID uuid, Player player) {
        PlayerAndTicks.removeIf(playerAttacks -> playerAttacks.getUuid().equals(uuid));
        player.sendMessage("你现在不会攻击了");
        return 1;
    }

    public static boolean hasUUIDFromList(UUID uuid) {
        for (PlayerAttacks playerAndTick : PlayerAndTicks) {
            if (playerAndTick.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}
