package realmayus.youmatter.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import realmayus.youmatter.replicator.ReplicatorContainer;
//import realmayus.youmatter.replicator.ContainerReplicator;

import java.util.function.Supplier;

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
            if (player.containerMenu instanceof ReplicatorContainer) {
                ReplicatorContainer openContainer = (ReplicatorContainer) player.containerMenu;
                openContainer.te.renderNext();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
