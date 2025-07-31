package org.wishtoday.rto.raidToOldVersion;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.tcoded.folialib.FoliaLib;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.wishtoday.rto.raidToOldVersion.Command.*;
import org.wishtoday.rto.raidToOldVersion.Event.RegisterEvent;
import org.wishtoday.rto.raidToOldVersion.Util.PlayerAttacks;

import java.util.*;

public final class RaidToOldVersion extends JavaPlugin implements Listener {
    private static RaidToOldVersion instance;
    private NamespacedKey shulkerInvKey;
    public static List<PlayerAttacks> PlayerAndTicks = new ArrayList<>();
    private static FoliaLib foliaLib;
    private ProtocolManager protocolManager;
    private PacketEventsAPI<?> packetEventsAPI;
    private PacketEventsSettings settings;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        foliaLib = new FoliaLib(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        saveDefaultConfig();
        //getCommand("tickattack").setExecutor(new TickAttack());
        registerCommands();
        packetEventsAPI.init();
        //getCommand("rto").setExecutor(new RTOCommand());
        shulkerInvKey = new NamespacedKey(this, "shulker_inventory");

        RegisterEvent.register(this, shulkerInvKey);
        getServer().getPluginManager().registerEvents(this, this);
        //protocolManager.addPacketListener(new PlaceBlockPacket(this));
        //new StructureRenderEvent().runTaskTimer(this, 0L, 20L);

    }

    @Override
    public void onLoad() {
        PacketEventsAPI<Plugin> build = SpigotPacketEventsBuilder.build(this);
        PacketEvents.setAPI(build);
        packetEventsAPI = build;
        settings = packetEventsAPI.getSettings();
        packetEventsAPI.load();
        settings.checkForUpdates(false);
        /*packetEventsAPI.getEventManager().registerListener(
                new EasyPlace(this), PacketListenerPriority.NORMAL
        );*/
        /*packetEventsAPI.getEventManager().registerListener(
                new CustomPayloadListener(), PacketListenerPriority.NORMAL
        );*/
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    public PacketEventsAPI<?> getPacketEventsAPI() {
        return packetEventsAPI;
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            RTOCommand.registerCommand(registrar);
            RulesCommand.registerCommand(registrar);
            SetKeepInventory.registerCommand(registrar);
            TickAttack.registerCommand(registrar);
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
        packetEventsAPI.terminate();
    }

    public boolean isHandInteractEnabled() {
        return true;
    }

    public boolean isInventoryInteractEnabled() {
        return true;
    }
}