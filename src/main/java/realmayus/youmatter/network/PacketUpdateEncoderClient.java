package realmayus.youmatter.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class PacketUpdateEncoderClient {
    public int energy;
    public int progress;

    public PacketUpdateEncoderClient(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.progress = buf.readInt();
    }

    public PacketUpdateEncoderClient(int energy, int progress) {
        this.energy = energy;
        this.progress = progress;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(progress);
    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandlers.handlePacketUpdateEncoderClient(this);
        });
        ctx.get().setPacketHandled(true);
    }
}
//todo make sure that sync works correctly
//todo need to revamp some packets