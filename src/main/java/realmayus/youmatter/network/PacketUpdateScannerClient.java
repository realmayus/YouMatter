package realmayus.youmatter.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketUpdateScannerClient {
    public int energy;
    public int progress;
    public boolean hasEncoder;

    public PacketUpdateScannerClient(PacketBuffer buf) {
        this.energy = buf.readInt();
        this.progress = buf.readInt();
        this.hasEncoder = buf.readBoolean();
    }

    public PacketUpdateScannerClient(int energy, int progress, boolean hasEncoder) {
        this.energy = energy;
        this.progress = progress;
        this.hasEncoder = hasEncoder;
    }

    void encode(PacketBuffer buf) {
        buf.writeInt(energy);
        buf.writeInt(progress);
        buf.writeBoolean(hasEncoder);
    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandlers.handlePacketUpdateScannerClient(this);
        });
        ctx.get().setPacketHandled(true);

    }﻿

}
