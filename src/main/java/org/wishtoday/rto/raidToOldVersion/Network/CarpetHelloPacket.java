package org.wishtoday.rto.raidToOldVersion.Network;

import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPluginMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

public class CarpetHelloPacket {
    private static final String CHANNEL = "carpet:hello";
    private static final ResourceLocation NAME = new ResourceLocation("carpet", "hello");


    public void sendHelloPacket(Player player) {
        byte[] bytes = {
                0x0A, 0x00, 0x00,  // Tag_Compound, name length 0
                0x00               // TAG_End
        };
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        WrapperPlayServerPluginMessage wrapperPlayServerPluginMessage = new WrapperPlayServerPluginMessage(NAME, buf.array());
        RaidToOldVersion.getInstance().getPacketEventsAPI().getPlayerManager().sendPacket(player, wrapperPlayServerPluginMessage);
    }
}
