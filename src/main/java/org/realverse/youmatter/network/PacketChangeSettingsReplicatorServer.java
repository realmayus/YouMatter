package org.realverse.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.realverse.youmatter.YouMatter;

public record PacketChangeSettingsReplicatorServer(boolean isActivated, boolean mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketChangeSettingsReplicatorServer> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "packet_settings_replicator"));
    public static final StreamCodec<ByteBuf, PacketChangeSettingsReplicatorServer> STREAM_CODEC;

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, PacketChangeSettingsReplicatorServer::isActivated, ByteBufCodecs.BOOL, PacketChangeSettingsReplicatorServer::mode, PacketChangeSettingsReplicatorServer::new);
    }
}
