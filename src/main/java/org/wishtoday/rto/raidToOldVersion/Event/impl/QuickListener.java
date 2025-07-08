package org.wishtoday.rto.raidToOldVersion.Event.impl;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.wishtoday.rto.raidToOldVersion.Config.Config;
import org.wishtoday.rto.raidToOldVersion.Util.QuickUtils;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import static org.wishtoday.rto.raidToOldVersion.Util.FoliaUtils.tryRunTask;

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
        if (Config.isNotQuickShulker()) return;
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

        tryOpenShulker2(itemInHand, player);
        QuickUtils.tryOpenWorkbench(plugin, itemInHand, player);
        QuickUtils.tryOpenEnderChest(plugin, itemInHand, player);
        QuickUtils.tryOpenSmithingTable(plugin, itemInHand, player);
        QuickUtils.tryOpenStonecutter(plugin, itemInHand, player);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryRight(InventoryClickEvent event) {
        if (Config.isNotQuickShulker()) return;
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
        if (!QuickUtils.isSupportItemAndTrueConfig(clickedItem)) return;
        Player player = (Player) event.getWhoClicked();
        OpenedShulker openedShulker = openedShulkers.get(player.getUniqueId());
        String uuidOrNull = QuickUtils.getUUIDOrNull(clickedItem);
        if (openedShulker != null
                && uuidOrNull != null
                && uuidOrNull.equals(openedShulker.uuid)) {
            return;
        }
        event.setCancelled(true);
        if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) {
            player.closeInventory();
        }
        //if (tryOpenShulker(clickedItem, player)) return;
        if (tryOpenShulker2(clickedItem, player)) return;
        //player.getEnderChest().setContents();
        QuickUtils.tryOpenWorkbench(plugin, clickedItem, player);
        QuickUtils.tryOpenEnderChest(plugin, clickedItem, player);
        QuickUtils.tryOpenSmithingTable(plugin, clickedItem, player);
        QuickUtils.tryOpenStonecutter(plugin, clickedItem, player);
    }

    @EventHandler
    public void onItemQuickMove(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        if (action != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player player)) return;
        if (!hasOpenedShulker(player.getUniqueId())) return;
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();
        Inventory bottomInventory = view.getBottomInventory();
        if ((topInventory.getType() != InventoryType.SHULKER_BOX
                && bottomInventory.getType() != InventoryType.PLAYER)
                ||
                !(topInventory.getType() != InventoryType.PLAYER
                        && bottomInventory.getType() != InventoryType.SHULKER_BOX))
            return;
        tryRunTask(plugin, () -> {
            trySaveShulker(topInventory, player);
        });
        /*Bukkit.getServer().getScheduler().runTask(plugin, () -> {
            trySaveShulker(topInventory, player);
        });*/
    }

    @EventHandler
    private void onItemMove(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        if (clickedInventory == null) return;
        if (clickedInventory.getType() != InventoryType.SHULKER_BOX) return;
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player player)) return;
        if (!hasOpenedShulker(player.getUniqueId())) return;
        tryRunTask(plugin, () -> {
            trySaveShulker(clickedInventory, player);
        });
        /*Bukkit.getServer().getScheduler().runTask(plugin, () -> {
            trySaveShulker(clickedInventory, player);
        });*/
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        if (isOpenedShulker(item)) {
            event.setCancelled(true);
        }
    }

    private boolean isOpenedShulker(Item item) {
        if (item == null) return false;
        ItemStack stack = item.getItemStack();
        if (!QuickUtils.isShulkerBox(stack)) return false;
        String s = stack.getItemMeta().getPersistentDataContainer().has(QuickUtils.ITEMUUID) ? stack.getItemMeta().getPersistentDataContainer().get(QuickUtils.ITEMUUID, PersistentDataType.STRING) : null;
        if (s == null) return false;
        boolean b = false;
        for (Map.Entry<UUID, OpenedShulker> entry : openedShulkers.entrySet()) {
            if (s.equals(entry.getValue().uuid)) {
                b = true;
                break;
            }
        }
        return b;
    }

    private boolean hasOpenedShulker(UUID uuid) {
        return openedShulkers.containsKey(uuid);
    }

    private boolean tryOpenShulker2(ItemStack clickedItem, Player player) {
        if (!Config.isCan_open_shulker()) return false;
        if (QuickUtils.isShulkerBox(clickedItem)) {
            String s = QuickUtils.getItemUUIDOrCreate(clickedItem);
            Inventory shulkerInv = QuickUtils.getShulkerInventory(clickedItem, shulkerInvKey);
            openedShulkers.put(player.getUniqueId(), new OpenedShulker(s, clickedItem, shulkerInv));
            tryRunTask(plugin, () -> {
                player.openInventory(shulkerInv);
            });
            /*if (HandySchedulerUtil.isFolia()) {
                HandySchedulerUtil.runTask(() -> player.openInventory(shulkerInv));
            }else {
                Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openInventory(shulkerInv));
            }*/
            //player.openInventory(shulkerInv);
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F);
            return true;
        }
        return false;
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInv = event.getInventory();
        ItemStack stack = trySaveShulker(closedInv, player);
        if (stack != null) QuickUtils.removeUUID(stack);
        if (closedInv == null) return;
        if (closedInv.equals(player.getEnderChest())) {
            QuickUtils.saveEnderChestInventory(closedInv, player);
        }
    }

    private @Nullable ItemStack trySaveShulker(Inventory closedInv, Player player) {
        UUID uuid = player.getUniqueId();
        OpenedShulker os = openedShulkers.get(uuid);
        if (os == null) {
            return null;
        }
        if (closedInv.equals(os.shulkerInventory)) {
            PlayerInventory playerInventory = player.getInventory();
            int slot = QuickUtils.findShulkerFromUUID(os.uuid, playerInventory);
            ItemStack item = playerInventory.getItem(slot);
            ItemStack updatedShulker = QuickUtils.updateShulkerItem(item, closedInv, shulkerInvKey);
            if (slot >= 0) {
                player.getInventory().setItem(slot, updatedShulker);
                ItemStack stack = player.getInventory().getItem(slot);
                return stack;
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
        return null;
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

        @Override
        public String toString() {
            return "OpenedShulker{" +
                    "uuid='" + uuid + '\'' +
                    ", shulkerItem=" + shulkerItem +
                    ", shulkerInventory=" + shulkerInventory +
                    '}';
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