package realmayus.youmatter.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import realmayus.youmatter.replicator.ReplicatorContainer;
//import realmayus.youmatter.replicator.ContainerReplicator;

import java.util.function.Supplier;

public class PacketShowNext {

    public PacketShowNext(PacketBuffer buf) {
    }
    public PacketShowNext() {
    }

    void encode(PacketBuffer buf) {

    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player.openContainer instanceof ReplicatorContainer) {
                ReplicatorContainer openContainer = (ReplicatorContainer) player.openContainer;
                openContainer.te.renderNext();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
