package org.wishtoday.rto.raidToOldVersion.Event.impl;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatEvent implements Listener {
    @SuppressWarnings("unused")
    @EventHandler
    public void onChat(AsyncChatEvent event) {
         Component message = event.message();
        //System.out.println(message);
    }
}
