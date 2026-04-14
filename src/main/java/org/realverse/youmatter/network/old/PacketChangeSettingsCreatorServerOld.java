package org.realverse.youmatter.network.old;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.realverse.youmatter.YouMatter;

public record PacketChangeSettingsCreatorServerOld(boolean isActive, boolean mode) implements CustomPacketPayload {
    public static final Type<PacketChangeSettingsCreatorServerOld> TYPE = new Type(ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "creator_settings_packet_old"));
    public static final StreamCodec<ByteBuf, PacketChangeSettingsCreatorServerOld> STREAM_CODEC;

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, PacketChangeSettingsCreatorServerOld::isActive, ByteBufCodecs.BOOL, PacketChangeSettingsCreatorServerOld::mode, PacketChangeSettingsCreatorServerOld::new);
    }
}
