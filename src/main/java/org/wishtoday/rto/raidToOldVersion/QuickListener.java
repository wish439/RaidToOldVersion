package org.wishtoday.rto.raidToOldVersion;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
            Inventory shulkerInv = QuickUtils.getShulkerInventory(itemInHand, shulkerInvKey);
            openedShulkers.put(player.getUniqueId(), new OpenedShulker(-1, itemInHand, shulkerInv));
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
        System.out.println(event.getAction().name());
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
        if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST)) {
            player.closeInventory();
        }
        if (QuickUtils.isShulkerBox(clickedItem)) {
            int slot = event.getSlot();

            Inventory shulkerInv = QuickUtils.getShulkerInventory(clickedItem, shulkerInvKey);
            openedShulkers.put(player.getUniqueId(), new OpenedShulker(slot, clickedItem, shulkerInv));
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
    public void onPlaceItem(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        if (!QuickUtils.isPlaceAction(action)) return;
        HumanEntity player = event.getWhoClicked();
        OpenedShulker shulker = openedShulkers.get(player.getUniqueId());
        if (shulker == null) return;
        System.out.println(shulker.getSlot());
        shulker.setSlot(event.getSlot());
        System.out.println(openedShulkers.get(player.getUniqueId()).getSlot());
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
            ItemStack updatedShulker = QuickUtils.updateShulkerItem(os.shulkerItem, closedInv, shulkerInvKey);
            if (os.slot >= 0) {
                player.getInventory().setItem(os.slot, updatedShulker);
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
        private int slot;
        private ItemStack shulkerItem;
        private Inventory shulkerInventory;

        public OpenedShulker(int slot, ItemStack shulkerItem, Inventory shulkerInventory) {
            this.slot = slot;
            this.shulkerItem = shulkerItem;
            this.shulkerInventory = shulkerInventory;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
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
