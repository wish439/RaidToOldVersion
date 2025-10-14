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
    public static final String CAN_OPEN_SHULKER = "quickshulker.can_open_shulker";
    public static final String CAN_OPEN_WORKBENCH = "quickshulker.can_open_Workbench";
    public static final String CAN_OPEN_ENDERCHEST = "quickshulker.can_open_enderchest";
    public static final String CAN_OPEN_SMITHINGTABLE = "quickshulker.can_open_smithingtable";
    public static final String CAN_OPEN_STONECUTTER = "quickshulker.can_open_stonecutter";
    private static boolean can_open_shulker = configuration.getBoolean(CAN_OPEN_SHULKER, true);
    private static boolean can_open_workbench = configuration.getBoolean(CAN_OPEN_WORKBENCH, true);
    private static boolean can_open_enderchest = configuration.getBoolean(CAN_OPEN_ENDERCHEST, true);
    private static boolean can_open_smithingtable = configuration.getBoolean(CAN_OPEN_SMITHINGTABLE, true);
    private static boolean can_open_stonecutter = configuration.getBoolean(CAN_OPEN_STONECUTTER, true);
    public static void reload() {
        File file = new File(RaidToOldVersion.getInstance().getDataFolder(), "config.yml");
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            configuration = RaidToOldVersion.getInstance().getConfig();
        }

        can_open_shulker = configuration.getBoolean(CAN_OPEN_SHULKER, true);
        can_open_workbench = configuration.getBoolean(CAN_OPEN_WORKBENCH, true);
        can_open_enderchest = configuration.getBoolean(CAN_OPEN_ENDERCHEST, true);
        can_open_smithingtable = configuration.getBoolean(CAN_OPEN_SMITHINGTABLE, true);
        can_open_stonecutter = configuration.getBoolean(CAN_OPEN_STONECUTTER, true);
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

    public static boolean isNotQuickShulker(){
        return !can_open_shulker
                && !can_open_workbench
                && !can_open_enderchest
                && !can_open_smithingtable
                && !can_open_stonecutter;
    }
}
