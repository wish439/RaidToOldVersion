package org.wishtoday.rto.raidToOldVersion.Command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SetKeepInventory implements CommandExecutor {
    private static final NamespacedKey KeepInventoryState =
            new NamespacedKey("raidtooldversion", "keep_inventory_state");
    private Component yes = Component.text("死亡不掉落已开启").color(NamedTextColor.GREEN);
    private Component no = Component.text("死亡不掉落已关闭").color(NamedTextColor.RED);

    @Override
    public boolean onCommand(@NotNull CommandSender sender
            , @NotNull Command command
            , @NotNull String label
            , @NotNull String[] args) {
        if (label.equalsIgnoreCase("setkeepinventory")) {
            if (sender instanceof Player player) {
                toggleTrueOrFalse(player);
                return true;
            }
        }
        return false;
    }

    private void toggleTrueOrFalse(Player player) {
        if (player == null) return;
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
