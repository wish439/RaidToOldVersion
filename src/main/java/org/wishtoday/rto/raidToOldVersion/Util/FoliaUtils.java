package org.wishtoday.rto.raidToOldVersion.Util;

import com.tcoded.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

public class FoliaUtils {
    public static void tryRunTask(Plugin plugin, Runnable runnable) {
        FoliaLib foliaLib = RaidToOldVersion.getFoliaLib();
        if (foliaLib.isFolia()) {
            foliaLib.getScheduler().runNextTick(wrappedTask -> {
                runnable.run();
            });
            return;
        }
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
