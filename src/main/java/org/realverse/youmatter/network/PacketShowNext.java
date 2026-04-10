package org.realverse.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.realverse.youmatter.YouMatter;

public record PacketShowNext() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketShowNext> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "packet_show_next"));
    public static final StreamCodec<ByteBuf, PacketShowNext> STREAM_CODEC = StreamCodec.unit(new PacketShowNext());

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
