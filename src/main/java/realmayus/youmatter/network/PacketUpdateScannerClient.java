package realmayus.youmatter.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


public class PacketUpdateScannerClient {
    public int energy;
    public int progress;
    public boolean hasEncoder;

    public PacketUpdateScannerClient(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.progress = buf.readInt();
        this.hasEncoder = buf.readBoolean();
    }

    public PacketUpdateScannerClient(int energy, int progress, boolean hasEncoder) {
        this.energy = energy;
        this.progress = progress;
        this.hasEncoder = hasEncoder;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(progress);
        buf.writeBoolean(hasEncoder);
    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandlers.handlePacketUpdateScannerClient(this);
        });
        ctx.get().setPacketHandled(true);

    }
}
