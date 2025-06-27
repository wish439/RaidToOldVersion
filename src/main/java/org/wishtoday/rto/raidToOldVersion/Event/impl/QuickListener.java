package org.wishtoday.rto.raidToOldVersion.Event.impl;

import org.bukkit.Bukkit;
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

/**
 * 快捷潜影盒事件监听器类
 * 处理玩家与潜影盒及其他容器（工作台、末影箱等）的交互逻辑
 */
public class QuickListener implements Listener {

    // 插件主类实例
    private final RaidToOldVersion plugin;
    // 用于存储潜影盒库存数据的命名空间键
    private final NamespacedKey shulkerInvKey;
    // 存储已打开潜影盒的玩家UUID和潜影盒信息
    private final Map<UUID, OpenedShulker> openedShulkers = new HashMap<>();

    /**
     * 构造函数
     * @param plugin 插件主类实例
     * @param shulkerInvKey 潜影盒库存数据键
     */
    public QuickListener(RaidToOldVersion plugin, NamespacedKey shulkerInvKey) {
        this.plugin = plugin;
        this.shulkerInvKey = shulkerInvKey;
    }

    /**
     * 处理玩家在非GUI环境下的交互事件（如右键空气）
     * @param event 玩家交互事件
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 检查插件是否启用快捷潜影盒功能
        if (Config.isNotQuickShulker()) return;

        // 检查手持交互功能是否启用
        if (!plugin.isHandInteractEnabled()) {
            return;
        }

        // 只处理右键空气事件
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();

        // 检查玩家手持物品是否有效
        ItemStack itemInHand = event.getItem();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            return;
        }

        // 检查物品是否支持快捷打开
        if (!QuickUtils.isSupportItem(itemInHand)) {
            return;
        }

        // 检查物品类型对应的功能是否在配置中启用
        if (!QuickUtils.isSupportItemAndTrueConfig(itemInHand)) return;

        // 取消原版事件（防止原版行为）
        event.setCancelled(true);

        // 尝试打开各种支持的容器
        tryOpenShulker2(itemInHand, player);  // 尝试打开潜影盒
        QuickUtils.tryOpenWorkbench(plugin, itemInHand, player);  // 尝试打开工作台
        QuickUtils.tryOpenEnderChest(plugin, itemInHand, player);  // 尝试打开末影箱
        QuickUtils.tryOpenSmithingTable(plugin, itemInHand, player);  // 尝试打开锻造台
        QuickUtils.tryOpenStonecutter(plugin, itemInHand, player);  // 尝试打开切石机
    }

    /**
     * 处理玩家在背包内的右键点击事件
     * @param event 背包点击事件
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryRight(InventoryClickEvent event) {
        // 检查插件是否启用快捷潜影盒功能
        if (Config.isNotQuickShulker()) return;

        // 确保点击的是玩家背包
        if (event.getClickedInventory() == null
                || event.getClickedInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        // 只处理右键点击事件
        if (event.getClick() != ClickType.RIGHT) {
            return;
        }

        // 检查背包交互功能是否启用
        if (!plugin.isInventoryInteractEnabled()) {
            return;
        }

        // 检查点击的物品是否有效
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        // 获取点击的物品
        ItemStack clickedItem = event.getCurrentItem();

        // 检查物品是否支持快捷打开
        if (!QuickUtils.isSupportItem(clickedItem)) {
            return;
        }

        // 检查物品类型对应的功能是否在配置中启用
        if (!QuickUtils.isSupportItemAndTrueConfig(clickedItem)) return;

        // 获取玩家
        Player player = (Player) event.getWhoClicked();

        // 检查是否已经打开了同一个潜影盒
        OpenedShulker openedShulker = openedShulkers.get(player.getUniqueId());
        String uuidOrNull = QuickUtils.getUUIDOrNull(clickedItem);
        if (openedShulker != null
                && uuidOrNull != null
                && uuidOrNull.equals(openedShulker.uuid)) {
            return; // 防止重复打开同一个潜影盒
        }

        // 取消原版事件
        event.setCancelled(true);

        // 如果玩家当前打开了潜影盒，先关闭它
        if (player.getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) {
            player.closeInventory();
        }

        // 尝试打开潜影盒（如果成功则返回）
        if (tryOpenShulker2(clickedItem, player)) return;

        // 尝试打开其他支持的容器
        QuickUtils.tryOpenWorkbench(plugin, clickedItem, player);
        QuickUtils.tryOpenEnderChest(plugin, clickedItem, player);
        QuickUtils.tryOpenSmithingTable(plugin, clickedItem, player);
        QuickUtils.tryOpenStonecutter(plugin, clickedItem, player);
    }

    /**
     * 处理快速转移物品事件（Shift+点击）
     * @param event 背包点击事件
     */
    @EventHandler
    public void onItemQuickMove(InventoryClickEvent event) {
        // 只处理快速转移动作（Shift+点击）
        InventoryAction action = event.getAction();
        if (action != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;

        // 确保操作者是玩家
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player player)) return;

        // 检查玩家是否打开了潜影盒
        if (!hasOpenedShulker(player.getUniqueId())) return;

        // 获取视图中的上下部分库存
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();
        Inventory bottomInventory = view.getBottomInventory();

        // 检查转移方向（从玩家背包到潜影盒或反之）
        if ((topInventory.getType() != InventoryType.SHULKER_BOX
                && bottomInventory.getType() != InventoryType.PLAYER)
                ||
                !(topInventory.getType() != InventoryType.PLAYER
                        && bottomInventory.getType() != InventoryType.SHULKER_BOX))
            return;

        // 延迟保存潜影盒内容（确保转移完成）
        Bukkit.getServer().getScheduler().runTask(plugin, () -> {
            trySaveShulker(topInventory, player);
        });
    }

    /**
     * 处理潜影盒内的物品移动事件
     * @param event 背包点击事件
     */
    @EventHandler
    private void onItemMove(InventoryClickEvent event) {
        // 获取被点击的库存
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        // 只处理潜影盒内的点击
        if (clickedInventory.getType() != InventoryType.SHULKER_BOX) return;

        // 排除快速转移事件（由其他处理器处理）
        InventoryAction action = event.getAction();
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) return;

        // 确保操作者是玩家
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player player)) return;

        // 检查玩家是否打开了潜影盒
        if (!hasOpenedShulker(player.getUniqueId())) return;

        // 延迟保存潜影盒内容（确保移动完成）
        Bukkit.getServer().getScheduler().runTask(plugin, () -> {
            trySaveShulker(clickedInventory, player);
        });
    }

    /**
     * 处理玩家丢弃物品事件
     * @param event 丢弃物品事件
     */
    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        // 检查丢弃的是否是已打开的潜影盒
        Item item = event.getItemDrop();
        if (isOpenedShulker(item)) {
            // 取消丢弃（防止玩家丢弃已打开的潜影盒）
            event.setCancelled(true);
        }
    }

    /**
     * 检查物品是否是已打开的潜影盒
     * @param item 掉落物实体
     * @return 如果是已打开的潜影盒返回true，否则false
     */
    private boolean isOpenedShulker(Item item) {
        if (item == null) return false;

        // 获取物品堆
        ItemStack stack = item.getItemStack();

        // 检查是否是潜影盒
        if (!QuickUtils.isShulkerBox(stack)) return false;

        // 获取物品的UUID
        String s = stack.getItemMeta().getPersistentDataContainer().has(QuickUtils.ITEMUUID) ?
                stack.getItemMeta().getPersistentDataContainer().get(QuickUtils.ITEMUUID, PersistentDataType.STRING) : null;
        if (s == null) return false;

        // 检查是否有玩家打开了这个潜影盒
        boolean isOpened = false;
        for (Map.Entry<UUID, OpenedShulker> entry : openedShulkers.entrySet()) {
            if (s.equals(entry.getValue().uuid)) {
                isOpened = true;
                break;
            }
        }

        return isOpened;
    }

    /**
     * 检查玩家是否打开了潜影盒
     * @param uuid 玩家UUID
     * @return 如果打开返回true，否则false
     */
    private boolean hasOpenedShulker(UUID uuid) {
        return openedShulkers.containsKey(uuid);
    }

    /**
     * 尝试打开潜影盒
     * @param clickedItem 被点击的物品（潜影盒）
     * @param player 玩家
     * @return 如果成功打开返回true，否则false
     */
    private boolean tryOpenShulker2(ItemStack clickedItem, Player player) {
        // 检查潜影盒功能是否启用
        if (!Config.isCan_open_shulker()) return false;

        // 检查物品是否是潜影盒
        if (QuickUtils.isShulkerBox(clickedItem)) {
            // 获取或创建潜影盒的UUID
            String uuid = QuickUtils.getItemUUIDOrCreate(clickedItem);

            // 获取潜影盒的库存
            Inventory shulkerInv = QuickUtils.getShulkerInventory(clickedItem, shulkerInvKey);

            // 记录打开的潜影盒
            openedShulkers.put(player.getUniqueId(), new OpenedShulker(uuid, clickedItem, shulkerInv));

            // 延迟打开库存（确保事件处理完成）
            Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openInventory(shulkerInv));

            // 播放打开音效
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    /**
     * 处理库存关闭事件
     * @param event 库存关闭事件
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInv = event.getInventory();

        // 尝试保存潜影盒内容
        ItemStack stack = trySaveShulker(closedInv, player);

        // 如果保存成功，移除UUID（如果需要）
        if (stack != null) QuickUtils.removeUUID(stack);

        // 如果是末影箱，保存其内容
        if (closedInv == null) return;
        if (closedInv.equals(player.getEnderChest())) {
            QuickUtils.saveEnderChestInventory(closedInv, player);
        }
    }

    /**
     * 尝试保存潜影盒内容
     * @param closedInv 关闭的库存
     * @param player 玩家
     * @return 更新后的潜影盒物品堆（如果保存成功）
     */
    private @Nullable ItemStack trySaveShulker(Inventory closedInv, Player player) {
        UUID playerUuid = player.getUniqueId();
        OpenedShulker os = openedShulkers.get(playerUuid);
        if (os == null) {
            return null; // 玩家没有打开潜影盒
        }

        // 检查关闭的是否是当前打开的潜影盒
        if (closedInv.equals(os.shulkerInventory)) {
            PlayerInventory playerInventory = player.getInventory();

            // 根据UUID查找潜影盒的位置
            int slot = QuickUtils.findShulkerFromUUID(os.uuid, playerInventory);

            // 获取潜影盒物品
            ItemStack item = playerInventory.getItem(slot);

            // 更新潜影盒内容
            ItemStack updatedShulker = QuickUtils.updateShulkerItem(item, closedInv, shulkerInvKey);

            if (slot >= 0) {
                // 更新背包中的潜影盒
                player.getInventory().setItem(slot, updatedShulker);
                ItemStack stack = player.getInventory().getItem(slot);
                openedShulkers.remove(playerUuid); // 移除记录
                return stack;
            } else {
                // 尝试更新主手或副手的潜影盒
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                if (mainHand.getType() != Material.AIR && QuickUtils.isShulkerBox(mainHand)) {
                    player.getInventory().setItemInMainHand(updatedShulker);
                } else {
                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    if (offHand.getType() != Material.AIR && QuickUtils.isShulkerBox(offHand)) {
                        player.getInventory().setItemInOffHand(updatedShulker);
                    }
                }
                openedShulkers.remove(playerUuid); // 移除记录
            }
        }
        return null;
    }

    /**
     * 表示已打开的潜影盒的内部类
     */
    private static class OpenedShulker {
        private String uuid; // 潜影盒的唯一标识
        private ItemStack shulkerItem; // 潜影盒物品堆
        private Inventory shulkerInventory; // 潜影盒的库存

        /**
         * 构造函数
         * @param uuid 潜影盒UUID
         * @param shulkerItem 潜影盒物品
         * @param shulkerInventory 潜影盒库存
         */
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

        // Getter 和 Setter 方法
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