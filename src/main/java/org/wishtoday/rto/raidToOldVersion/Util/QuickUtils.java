package org.wishtoday.rto.raidToOldVersion.Util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wishtoday.rto.raidToOldVersion.Config.Config;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

import java.util.UUID;

import static org.wishtoday.rto.raidToOldVersion.Util.FoliaUtils.tryRunTask;


public class QuickUtils {
    public static final NamespacedKey ITEMUUID = new NamespacedKey(RaidToOldVersion.getInstance(), "item_uuid");

    public static boolean isSupportItem(ItemStack item) {
        return isShulkerBox(item)
                || isCraftingTable(item)
                || isEnderChest(item)
                || isSmithingTable(item)
                || isStonecutter(item);
    }

    public static boolean isSupportItemAndTrueConfig(ItemStack item) {
        return isShulkerAndTrueConfig(item)
                || isCraftingTableAndTrueConfig(item)
                || isEnderChestAndTrueConfig(item)
                || isSmithingTableAndTrueConfig(item)
                || isStonecutterAndTrueConfig(item);
    }
    public static boolean isStonecutter(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.STONECUTTER;
    }
    public static boolean isStonecutterAndTrueConfig(ItemStack item) {
        return isStonecutter(item) && Config.isCan_open_stonecutter();
    }

    public static boolean isSmithingTable(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.SMITHING_TABLE;
    }

    public static boolean isSmithingTableAndTrueConfig(ItemStack item) {
        return isSmithingTable(item) && Config.isCan_open_smithingtable();
    }

    public static boolean isShulkerBox(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type.name().endsWith("SHULKER_BOX");
    }

    public static boolean isShulkerAndTrueConfig(ItemStack item) {
        return isShulkerBox(item) && Config.isCan_open_shulker();
    }

    public static boolean isCraftingTable(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.CRAFTING_TABLE && item.getAmount() == 1;
    }

    public static boolean isCraftingTableAndTrueConfig(ItemStack item) {
        return isCraftingTable(item) && Config.isCan_open_workbench();
    }

    public static boolean isEnderChest(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.ENDER_CHEST && item.getAmount() == 1;
    }

    public static boolean isEnderChestAndTrueConfig(ItemStack item) {
        return isEnderChest(item) && Config.isCan_open_enderchest();
    }

    public static Inventory getShulkerInventory(ItemStack item, NamespacedKey key) {

        if (item == null) return Bukkit.createInventory(null, InventoryType.SHULKER_BOX, Component.text("shulker"));
        Component name = QuickUtils.getItemName(item);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Bukkit.createInventory(null, InventoryType.SHULKER_BOX, name);

        if (meta instanceof BlockStateMeta blockStateMeta) {
            BlockState blockState = blockStateMeta.getBlockState();

            if (blockState instanceof ShulkerBox shulkerBox) {
                Inventory shulkerInventory = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, name);

                ItemStack[] contents = shulkerBox.getInventory().getContents();
                shulkerInventory.setContents(contents);

                return shulkerInventory;
            }
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            String serialized = container.get(key, PersistentDataType.STRING);
            Inventory loadedInv = deserializeInventory(serialized);
            if (loadedInv != null) {
                Inventory namedInventory = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, name);
                namedInventory.setContents(loadedInv.getContents());
                return namedInventory;
            }
        }

        return Bukkit.createInventory(null, InventoryType.SHULKER_BOX, name);
    }

    public static void saveShulkerInventory(ItemStack item, Inventory inv, NamespacedKey key) {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta instanceof BlockStateMeta blockStateMeta) {
            BlockState blockState = blockStateMeta.getBlockState();

            if (blockState instanceof ShulkerBox shulkerBox) {
                shulkerBox.getInventory().setContents(inv.getContents());

                blockStateMeta.setBlockState(shulkerBox);
                item.setItemMeta(blockStateMeta);
                return;
            }
        }

        String serialized = serializeInventory(inv);
        if (serialized == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, serialized);
        item.setItemMeta(meta);
    }

    public static void saveEnderChestInventory(Inventory inv, Player player) {
        player.getEnderChest().setContents(inv.getContents());
    }

    public static void openPlayerEnderChest(Player player, RaidToOldVersion plugin) {
        if (player.getOpenInventory().getTopInventory().getType() == InventoryType.ENDER_CHEST) {
            player.closeInventory();
        }
        if (player.getOpenInventory().getTopInventory().getType() == InventoryType.SHULKER_BOX) {
            player.closeInventory();
        }
        tryRunTask(plugin, () -> {
            player.openInventory(player.getEnderChest());
        });
        /*plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.openInventory(player.getEnderChest());
        });*/
    }

    public static ItemStack updateShulkerItem(ItemStack item, Inventory inv, NamespacedKey key) {
        if (item == null) return null;
        ItemStack newItem = item.clone();
        saveShulkerInventory(newItem, inv, key);
        return newItem;
    }

    private static String serializeInventory(Inventory inv) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("size", inv.getSize());
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item != null) {
                    config.set("item" + i, item);
                }
            }
            return config.saveToString();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error inventories: " + e.getMessage());
            return null;
        }
    }

    private static Inventory deserializeInventory(String data) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(data);
            int size = config.getInt("size", 27);
            Inventory inv = Bukkit.createInventory(null, InventoryType.SHULKER_BOX);
            for (int i = 0; i < size; i++) {
                if (config.contains("item" + i)) {
                    ItemStack item = config.getItemStack("item" + i);
                    inv.setItem(i, item);
                }
            }
            return inv;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Inventory serialization error: " + e.getMessage());
            return null;
        }
    }

    public static Component getItemName(@NotNull ItemStack it) {
        final ItemMeta meta = it.getItemMeta();

        if (meta == null || meta.displayName() == null) return Component.text("潜影盒");

        return meta.displayName();
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static int findShulkerFromUUID(String uuid, Inventory inv) {
        int i = -1;
        int size = inv.getSize();
        for (int i1 = 0; i1 < size; i1++) {
            ItemStack stack = inv.getItem(i1);
            if (isShulkerBox(stack)) {
                ItemMeta meta = stack.getItemMeta();
                if (meta == null) continue;
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (!container.has(ITEMUUID, PersistentDataType.STRING)) continue;
                String uuid1 = container.get(ITEMUUID, PersistentDataType.STRING);
                if (!uuid.equals(uuid1)) continue;
                i = i1;
            }
        }
        return i;
    }

    @Nullable
    public static String getItemUUIDOrCreate(ItemStack stack) {
        String uuid;
        if (stack == null) return null;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(ITEMUUID, PersistentDataType.STRING)) {
            uuid = container.get(ITEMUUID, PersistentDataType.STRING);
        } else {
            uuid = createUUID(stack);
        }
        return uuid;
    }

    @Nullable
    public static String createUUID(ItemStack itemStack) {
        if (itemStack == null) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String uuid = getRandomUUID();
        container.set(ITEMUUID, PersistentDataType.STRING, uuid);
        itemStack.setItemMeta(meta);
        return uuid;
    }

    public static void removeUUID(ItemStack stack) {
        if (stack == null) return;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(ITEMUUID, PersistentDataType.STRING)) {
            container.remove(ITEMUUID);
        }
        stack.setItemMeta(meta);
    }

    public static boolean hasUUID(ItemStack stack) {
        if (stack == null) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(ITEMUUID, PersistentDataType.STRING);
    }

    public static String getUUIDOrDefault(ItemStack stack, String defaultValue) {
        if (stack == null) return defaultValue;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return defaultValue;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!hasUUID(stack)) return defaultValue;
        return container.get(ITEMUUID, PersistentDataType.STRING);
    }

    public static String getUUIDOrNull(ItemStack stack) {
        return getUUIDOrDefault(stack, null);
    }

    public static void tryOpenWorkbench(RaidToOldVersion plugin, Player player) {
        if (!Config.isCan_open_workbench()) return;
        tryRunTask(plugin, () -> player.openWorkbench(null, true));
        /*Bukkit.getServer().getScheduler().runTask(plugin,
                () -> player.openWorkbench(null, true));*/
    }

    public static void tryOpenWorkbench(RaidToOldVersion plugin, ItemStack itemInHand, Player player) {
        if (!Config.isCan_open_workbench()) return;
        if (QuickUtils.isCraftingTable(itemInHand)) {
            tryRunTask(plugin, () -> {
                player.openWorkbench(null, true);
            });
            //Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openWorkbench(null, true));
        }
    }

    public static void tryOpenEnderChest(RaidToOldVersion plugin, Player player) {
        if (!Config.isCan_open_enderchest()) return;
        QuickUtils.openPlayerEnderChest(player, plugin);
    }

    public static void tryOpenEnderChest(RaidToOldVersion plugin, ItemStack itemInHand, Player player) {
        if (!Config.isCan_open_enderchest()) return;
        if (QuickUtils.isEnderChest(itemInHand)) {
            QuickUtils.openPlayerEnderChest(player, plugin);
        }
    }

    public static void tryOpenSmithingTable(RaidToOldVersion plugin, Player player) {
        if (!Config.isCan_open_smithingtable()) return;
        tryRunTask(plugin, () -> {
            player.openSmithingTable(null, true);
        });
        //Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openSmithingTable(null, true));
    }

    public static void tryOpenSmithingTable(RaidToOldVersion plugin, ItemStack itemInHand, Player player) {
        if (!Config.isCan_open_smithingtable()) return;
        if (QuickUtils.isSmithingTable(itemInHand)) {
            tryRunTask(plugin, () -> {
                player.openSmithingTable(null, true);
            });
            //Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openSmithingTable(null, true));
        }
    }

    public static void tryOpenStonecutter(RaidToOldVersion plugin, Player player) {
        if (!Config.isCan_open_stonecutter()) return;
        tryRunTask(plugin, () -> {
            player.openStonecutter(null, true);
        });
        //Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openStonecutter(null, true));
    }
    public static void tryOpenStonecutter(RaidToOldVersion plugin, ItemStack itemInHand, Player player) {
        if (!Config.isCan_open_stonecutter()) return;
        if (isStonecutter(itemInHand)) {
            tryRunTask(plugin, () -> {
                player.openStonecutter(null, true);
            });
            //Bukkit.getServer().getScheduler().runTask(plugin, () -> player.openStonecutter(null, true));
        }
    }
}