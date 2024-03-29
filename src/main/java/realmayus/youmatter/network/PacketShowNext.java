package realmayus.youmatter.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import realmayus.youmatter.replicator.ReplicatorMenu;
//import realmayus.youmatter.replicator.ContainerReplicator;

public class PacketShowNext {

    public PacketShowNext(FriendlyByteBuf buf) {
    }
    public PacketShowNext() {
    }

    void encode(FriendlyByteBuf buf) {

    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player.containerMenu instanceof ReplicatorMenu openContainer) {
                openContainer.replicator.renderNext();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
