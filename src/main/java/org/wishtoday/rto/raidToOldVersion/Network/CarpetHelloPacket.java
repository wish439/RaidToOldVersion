package org.wishtoday.rto.raidToOldVersion.Network;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.CustomPacketPayloadWrapper;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.wishtoday.rto.raidToOldVersion.RaidToOldVersion;

@Deprecated
@SuppressWarnings("all")
public class CarpetHelloPacket {
    private static final String CHANNEL = "carpet:hello";
    private static final MinecraftKey NAME = new MinecraftKey("carpet", "hello");


    public void sendHelloPacket(Player player) {
        byte[] bytes = {
                0x0A, 0x00, 0x00,  // Tag_Compound, name length 0
                0x00               // TAG_End
        };
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        ProtocolManager manager = RaidToOldVersion.getInstance().getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.DEBUG_SAMPLE);
        packet.getMinecraftKeys().write(0,NAME);
        //MinecraftReflection.getPacketDataSerializer()
        manager.sendServerPacket(player, packet);
    }
}
