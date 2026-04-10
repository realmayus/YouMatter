package org.realverse.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.realverse.youmatter.YouMatter;

public record PacketChangeSettingsCreatorServer(boolean isActivated) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketChangeSettingsCreatorServer> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "creator_settings_packet"));
    public static final StreamCodec<ByteBuf, PacketChangeSettingsCreatorServer> STREAM_CODEC;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, PacketChangeSettingsCreatorServer::isActivated, PacketChangeSettingsCreatorServer::new);
    }
}
