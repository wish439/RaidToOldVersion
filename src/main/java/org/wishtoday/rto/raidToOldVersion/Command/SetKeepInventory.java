package org.wishtoday.rto.raidToOldVersion.Command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SetKeepInventory {
    private static final NamespacedKey KeepInventoryState =
            new NamespacedKey("raidtooldversion", "keep_inventory_state");
    private static Component yes = Component.text("死亡不掉落已开启").color(NamedTextColor.GREEN);
    private static Component no = Component.text("死亡不掉落已关闭").color(NamedTextColor.RED);

    public static void registerCommand(Commands commands) {
        commands.register(
                Commands.literal("setkeepinventory").executes(SetKeepInventory::toggleTrueOrFalse).build()
        );
    }

    private static int toggleTrueOrFalse(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("此指令只有玩家可使用");
            return 0;
        }
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (container.has(KeepInventoryState, PersistentDataType.BOOLEAN)) {
            Boolean b = container.get(KeepInventoryState, PersistentDataType.BOOLEAN);
            if (b != null) {
                container.set(KeepInventoryState, PersistentDataType.BOOLEAN, !b);
            }
        } else {
            container.set(KeepInventoryState, PersistentDataType.BOOLEAN, Boolean.TRUE);
        }
        Boolean b = getKeepInventoryState(player);
        player.sendMessage(b ? yes : no);
        return 1;
    }

    @NotNull
    public static Boolean getKeepInventoryState(Player player) {
        if (player == null) return Boolean.FALSE;
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (container.has(KeepInventoryState, PersistentDataType.BOOLEAN)) {
            Boolean b = container.get(KeepInventoryState, PersistentDataType.BOOLEAN);
            return b == null ? Boolean.FALSE : b;
        }
        return Boolean.FALSE;
    }
}
