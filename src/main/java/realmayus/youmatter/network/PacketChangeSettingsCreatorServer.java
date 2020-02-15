package realmayus.youmatter.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
//import realmayus.youmatter.creator.ContainerCreator;
import java.util.function.Supplier;

public class PacketChangeSettingsCreatorServer{

    private boolean isActivated;


    public PacketChangeSettingsCreatorServer(PacketBuffer buf) {
        isActivated = buf.readBoolean();
    }

    public PacketChangeSettingsCreatorServer(boolean isActivated) {
        this.isActivated = isActivated;
    }

    void encode(PacketBuffer buf) {
        buf.writeBoolean(isActivated);
    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
//        // This is the player the packet was sent to the server from
//        ctx.get().enqueueWork(() -> {
//            ServerPlayerEntity player = ctx.get().getSender();
//            if (player.openContainer instanceof ContainerCreator) {
//                ContainerCreator openContainer = (ContainerCreator) player.openContainer;
//                openContainer.te.setActivated(message.isActivated);
//            }
//        });
//        ctx.get().setPacketHandled(true);
    }
}
