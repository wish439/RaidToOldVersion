package org.wishtoday.rto.raidToOldVersion.Network;


public class CarpetHelloPacket {
    private static final String CHANNEL = "carpet:hello";
    //private static final ResourceLocation NAME = new ResourceLocation("carpet", "hello");


    /*public void sendHelloPacket(Player player) {
        byte[] bytes = {
                0x0A, 0x00, 0x00,  // Tag_Compound, name length 0
                0x00               // TAG_End
        };
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        WrapperPlayServerPluginMessage wrapperPlayServerPluginMessage = new WrapperPlayServerPluginMessage(NAME, buf.array());
        RaidToOldVersion.getInstance().getPacketEventsAPI().getPlayerManager().sendPacket(player, wrapperPlayServerPluginMessage);
    }*/
}
