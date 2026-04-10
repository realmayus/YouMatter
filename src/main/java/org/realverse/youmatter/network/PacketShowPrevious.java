package org.realverse.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.realverse.youmatter.YouMatter;

public record PacketShowPrevious() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketShowPrevious> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "packet_show_previous"));
    public static final StreamCodec<ByteBuf, PacketShowPrevious> STREAM_CODEC = StreamCodec.unit(new PacketShowPrevious());

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
