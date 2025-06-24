package org.wishtoday.rto.raidToOldVersion.Config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

import java.io.File;
import java.io.IOException;

public class Config {
    private static FileConfiguration configuration = RaidToOldVersion.getInstance().getConfig();
    public static final String RAID_TO_OLD_VERSION_CONFIG_KEY = "raidToOldVersion";
    public static final String XP_NO_COOLDOWN = "xp_no_cooldown";
    public static final String CAN_OPEN_SHULKER = "quickshulker.can_open_shulker";
    public static final String CAN_OPEN_WORKBENCH = "quickshulker.can_open_Workbench";
    public static final String CAN_OPEN_ENDERCHEST = "quickshulker.can_open_enderchest";
    public static final String RULES_TEXT = "texts.rules_text";
    private static boolean raid_to_old_version = configuration.getBoolean(RAID_TO_OLD_VERSION_CONFIG_KEY, true);
    private static boolean xp_no_cooldown = configuration.getBoolean(XP_NO_COOLDOWN, true);
    private static boolean can_open_shulker = configuration.getBoolean(CAN_OPEN_SHULKER, true);
    private static boolean can_open_workbench = configuration.getBoolean(CAN_OPEN_WORKBENCH, true);
    private static boolean can_open_enderchest = configuration.getBoolean(CAN_OPEN_ENDERCHEST, true);
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
        rules_text = configuration.getConfigurationSection(RULES_TEXT);
    }

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

    public static ConfigurationSection getRules_text() {
        return rules_text;
    }
    public static boolean isNotQuickShulker(){
        return !can_open_shulker && !can_open_workbench && !can_open_enderchest;
    }
}
