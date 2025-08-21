package org.wishtoday.rto.raidToOldVersion.Config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

import java.io.File;
import java.io.IOException;
@SuppressWarnings("SpellCheckingInspection")
public class Config {
    private static FileConfiguration configuration = RaidToOldVersion.getInstance().getConfig();
    public static final String RAID_TO_OLD_VERSION_CONFIG_KEY = "raidToOldVersion";
    public static final String XP_NO_COOLDOWN = "xp_no_cooldown";
    public static final String CAN_OPEN_SHULKER = "quickshulker.can_open_shulker";
    public static final String CAN_OPEN_WORKBENCH = "quickshulker.can_open_Workbench";
    public static final String CAN_OPEN_ENDERCHEST = "quickshulker.can_open_enderchest";
    public static final String CAN_OPEN_SMITHINGTABLE = "quickshulker.can_open_smithingtable";
    public static final String CAN_OPEN_STONECUTTER = "quickshulker.can_open_stonecutter";
    public static final String RULES_TEXT = "texts.rules_text";
    public static final String KEEP_INVENTORY_TOGGLE = "keep_inventory_toggle";
    private static boolean raid_to_old_version = configuration.getBoolean(RAID_TO_OLD_VERSION_CONFIG_KEY, true);
    private static boolean xp_no_cooldown = configuration.getBoolean(XP_NO_COOLDOWN, true);
    private static boolean can_open_shulker = configuration.getBoolean(CAN_OPEN_SHULKER, true);
    private static boolean can_open_workbench = configuration.getBoolean(CAN_OPEN_WORKBENCH, true);
    private static boolean can_open_enderchest = configuration.getBoolean(CAN_OPEN_ENDERCHEST, true);
    private static boolean can_open_smithingtable = configuration.getBoolean(CAN_OPEN_SMITHINGTABLE, true);
    private static boolean can_open_stonecutter = configuration.getBoolean(CAN_OPEN_STONECUTTER, true);
    private static boolean keep_inventory_toggle = configuration.getBoolean(KEEP_INVENTORY_TOGGLE, true);
    private static ConfigurationSection rules_text = configuration.getConfigurationSection(RULES_TEXT);
    public static void reload() {
        File file = new File(RaidToOldVersion.getInstance().getDataFolder(), "config.yml");
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            configuration = RaidToOldVersion.getInstance().getConfig();
        }

        raid_to_old_version = configuration.getBoolean(RAID_TO_OLD_VERSION_CONFIG_KEY, true);
        xp_no_cooldown = configuration.getBoolean(XP_NO_COOLDOWN, true);
        can_open_shulker = configuration.getBoolean(CAN_OPEN_SHULKER, true);
        can_open_workbench = configuration.getBoolean(CAN_OPEN_WORKBENCH, true);
        can_open_enderchest = configuration.getBoolean(CAN_OPEN_ENDERCHEST, true);
        can_open_smithingtable = configuration.getBoolean(CAN_OPEN_SMITHINGTABLE, true);
        can_open_stonecutter = configuration.getBoolean(CAN_OPEN_STONECUTTER, true);
        rules_text = configuration.getConfigurationSection(RULES_TEXT);
        keep_inventory_toggle = configuration.getBoolean(KEEP_INVENTORY_TOGGLE, true);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRaid_to_old_version() {
        return raid_to_old_version;
    }

    public static boolean isXp_no_cooldown() {
        return xp_no_cooldown;
    }

    public static boolean isCan_open_shulker() {
        return can_open_shulker;
    }

    public static boolean isCan_open_workbench() {
        return can_open_workbench;
    }

    public static boolean isCan_open_enderchest() {
        return can_open_enderchest;
    }
    public static boolean isCan_open_smithingtable() {
        return can_open_smithingtable;
    }
    public static boolean isCan_open_stonecutter() {
        return can_open_stonecutter;
    }

    public static ConfigurationSection getRules_text() {
        return rules_text;
    }
    public static boolean isNotQuickShulker(){
        return !can_open_shulker
                && !can_open_workbench
                && !can_open_enderchest
                && !can_open_smithingtable
                && !can_open_stonecutter;
    }

    public static boolean isKeep_inventory_toggle() {
        return keep_inventory_toggle;
    }
}
