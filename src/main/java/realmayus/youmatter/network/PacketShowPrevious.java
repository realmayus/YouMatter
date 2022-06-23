package realmayus.youmatter.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import realmayus.youmatter.replicator.ReplicatorMenu;
//import realmayus.youmatter.replicator.ContainerReplicator;

public class PacketShowPrevious {

    public PacketShowPrevious(FriendlyByteBuf buf) {
    }
    public PacketShowPrevious() {
    }

    void encode(FriendlyByteBuf buf) {

    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player.containerMenu instanceof ReplicatorMenu openContainer) {
                openContainer.replicator.renderPrevious();
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
