package realmayus.youmatter.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
//import realmayus.youmatter.replicator.ContainerReplicator;

import java.util.function.Supplier;

public class PacketShowPrevious {

    public PacketShowPrevious(PacketBuffer buf) {
    }
    public PacketShowPrevious() {
    }

    void encode(PacketBuffer buf) {

    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
//        ctx.get().enqueueWork(() -> {
//            ServerPlayerEntity player = ctx.get().getSender();
//            if (player.openContainer instanceof ContainerReplicator) {
//                ContainerReplicator openContainer = (ContainerReplicator) player.openContainer;
//                openContainer.te.renderPrevious();
//            }
//        }); TODO: setHandled
    }

}
