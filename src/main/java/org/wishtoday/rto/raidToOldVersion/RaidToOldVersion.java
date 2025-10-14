package org.wishtoday.rto.raidToOldVersion;

import com.tcoded.folialib.FoliaLib;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.wishtoday.rto.raidToOldVersion.Command.*;
import org.wishtoday.rto.raidToOldVersion.Event.RegisterEvent;

import java.util.*;

@SuppressWarnings({"CommentedOutCode", "SpellCheckingInspection"})
public final class RaidToOldVersion extends JavaPlugin implements Listener {
    private static RaidToOldVersion instance;
    private NamespacedKey shulkerInvKey;
    private static FoliaLib foliaLib;
    //private PacketEventsAPI<?> packetEventsAPI;
    //private PacketEventsSettings settings;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        foliaLib = new FoliaLib(this);
        saveDefaultConfig();
        //getCommand("tickattack").setExecutor(new TickAttack());
        registerCommands();
        //packetEventsAPI.init();
        //getCommand("rto").setExecutor(new RTOCommand());
        shulkerInvKey = new NamespacedKey(this, "shulker_inventory");

        RegisterEvent.register(this, shulkerInvKey);
        //getServer().getPluginManager().registerEvents(this, this);
        //protocolManager.addPacketListener(new PlaceBlockPacket(this));
        //new StructureRenderEvent().runTaskTimer(this, 0L, 20L);

    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }
    /*public PacketEventsAPI<?> getPacketEventsAPI() {
        return packetEventsAPI;
    }*/

    @SuppressWarnings("UnstableApiUsage")
    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            RTOCommand.registerCommand(registrar);
            QuickShulkerCommand.registerQuickCommands(registrar);
        });
    }

    public static RaidToOldVersion getInstance() {
        return instance;
    }

    public NamespacedKey getShulkerInvKey() {
        return shulkerInvKey;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll();
        foliaLib = null;
    }

    public boolean isHandInteractEnabled() {
        return true;
    }
}