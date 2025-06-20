package org.wishtoday.rto.raidToOldVersion.Event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.wishtoday.rto.raidToOldVersion.Util.QuickUtils;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class QuickListener implements Listener {

    private final RaidToOldVersion plugin;
    private final NamespacedKey shulkerInvKey;

    private final Map<UUID, OpenedShulker> openedShulkers = new HashMap<>();

    public QuickListener(RaidToOldVersion plugin, NamespacedKey shulkerInvKey) {
        this.plugin = plugin;
        this.shulkerInvKey = shulkerInvKey;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.isHandInteractEnabled()) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = event.getPlayer();

        ItemStack itemInHand = event.getItem();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            return;
        }
        if (!QuickUtils.isSupportItem(itemInHand)) {
            return;
        }
        event.setCancelled(true);

        if (QuickUtils.isShulkerBox(itemInHand)) {
            String s = QuickUtils.getItemUUIDOrCreate(itemInHand);
            Inventory shulkerInv = QuickUtils.getShulkerInventory(itemInHand, shulkerInvKey);
            openedShulkers.put(player.getUniqueId(), new OpenedShulker(s, itemInHand, shulkerInv));
            player.openInventory(shulkerInv);
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F);
        }
        if (QuickUtils.isCraftingTable(itemInHand)) {
            Bukkit.getServer().getScheduler().runTask(plugin,() -> player.openWorkbench(null,true));
        }
        if (QuickUtils.isEnderChest(itemInHand)) {
            QuickUtils.openPlayerEnderChest(player,plugin);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryRight(InventoryClickEvent event) {
        if (event.getClickedInventory() == null
                || event.getClickedInventory().getType() != InventoryType.PLAYER) {
            return;
        }
        if (event.getClick() != ClickType.RIGHT) {
            return;
        }
        if (!plugin.isInventoryInteractEnabled()) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        ItemStack clickedItem = event.getCurrentItem();
        if (!QuickUtils.isSupportItem(clickedItem)) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) {
            player.closeInventory();
        }
        if (QuickUtils.isShulkerBox(clickedItem)) {
            int slot = event.getSlot();
            String s = QuickUtils.getItemUUIDOrCreate(clickedItem);
            Inventory shulkerInv = QuickUtils.getShulkerInventory(clickedItem, shulkerInvKey);
            openedShulkers.put(player.getUniqueId(), new OpenedShulker(s, clickedItem, shulkerInv));
            Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openInventory(shulkerInv));
            //player.openInventory(shulkerInv);
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F);
            return;
        }
        //player.getEnderChest().setContents();
        if (QuickUtils.isCraftingTable(clickedItem)) {
            Bukkit.getServer().getScheduler().runTask(plugin,() -> player.openWorkbench(null,true));
            //player.openInventory(plugin.getServer().createInventory(null,InventoryType.WORKBENCH));
        }
        if (QuickUtils.isEnderChest(clickedItem)) {
            QuickUtils.openPlayerEnderChest(player,plugin);
        }
    }



    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        OpenedShulker os = openedShulkers.get(uuid);
        if (os == null) {
            return;
        }
        Inventory closedInv = event.getInventory();
        if (closedInv.equals(os.shulkerInventory)) {
            PlayerInventory playerInventory = player.getInventory();
            int slot = QuickUtils.findShulkerFromUUID(os.uuid, playerInventory);
            ItemStack item = playerInventory.getItem(slot);
            ItemStack updatedShulker = QuickUtils.updateShulkerItem(item, closedInv, shulkerInvKey);
            if (slot >= 0) {
                player.getInventory().setItem(slot, updatedShulker);
                ItemStack stack = player.getInventory().getItem(slot);
                QuickUtils.removeUUID(stack);
            } else {
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                if (mainHand.getType() != Material.AIR && QuickUtils.isShulkerBox(mainHand)) {
                    player.getInventory().setItemInMainHand(updatedShulker);
                } else {
                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    if (offHand.getType() != Material.AIR && QuickUtils.isShulkerBox(offHand)) {
                        player.getInventory().setItemInOffHand(updatedShulker);
                    }
                }
            }
            openedShulkers.remove(uuid);
        }
        if (closedInv.equals(player.getEnderChest())) {
            QuickUtils.saveEnderChestInventory(closedInv, player);
        }
    }

    private static class OpenedShulker {
        private String uuid;
        private ItemStack shulkerItem;
        private Inventory shulkerInventory;

        public OpenedShulker(String uuid, ItemStack shulkerItem, Inventory shulkerInventory) {
            this.uuid = uuid;
            this.shulkerItem = shulkerItem;
            this.shulkerInventory = shulkerInventory;
        }

        public String getUuid() {
            return uuid;
        }

        public void setSlot(String uuid) {
            this.uuid = uuid;
        }

        public ItemStack getShulkerItem() {
            return shulkerItem;
        }

        public void setShulkerItem(ItemStack shulkerItem) {
            this.shulkerItem = shulkerItem;
        }

        public Inventory getShulkerInventory() {
            return shulkerInventory;
        }

        public void setShulkerInventory(Inventory shulkerInventory) {
            this.shulkerInventory = shulkerInventory;
        }
    }
}
