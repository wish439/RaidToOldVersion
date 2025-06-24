package org.wishtoday.rto.raidToOldVersion;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.wishtoday.rto.raidToOldVersion.Command.RTOCommand;
import org.wishtoday.rto.raidToOldVersion.Command.RulesCommand;
import org.wishtoday.rto.raidToOldVersion.Command.SetKeepInventory;
import org.wishtoday.rto.raidToOldVersion.Command.TickAttack;
import org.wishtoday.rto.raidToOldVersion.Event.RegisterEvent;
import org.wishtoday.rto.raidToOldVersion.Util.PlayerAttacks;

import java.util.*;

public final class RaidToOldVersion extends JavaPlugin implements Listener {
    private static RaidToOldVersion instance;
    private NamespacedKey shulkerInvKey;
    public static List<PlayerAttacks> PlayerAndTicks = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        //getCommand("tickattack").setExecutor(new TickAttack());
        registerCommands();
        //getCommand("rto").setExecutor(new RTOCommand());
        shulkerInvKey = new NamespacedKey(this, "shulker_inventory");

        RegisterEvent.register(this, shulkerInvKey);
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            RTOCommand.registerCommand(registrar);
            RulesCommand.registerCommand(registrar);
            SetKeepInventory.registerCommand(registrar);
            TickAttack.registerCommand(registrar);
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
        // Plugin shutdown logic
    }

    public boolean isHandInteractEnabled() {
        return true;
    }

    public boolean isInventoryInteractEnabled() {
        return true;
    }
}