package realmayus.youmatter.network;

//import realmayus.youmatter.creator.ContainerCreator;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import realmayus.youmatter.creator.CreatorMenu;

public class PacketChangeSettingsCreatorServer{

    private boolean isActivated;


    public PacketChangeSettingsCreatorServer(FriendlyByteBuf buf) {
        isActivated = buf.readBoolean();
    }

    public PacketChangeSettingsCreatorServer(boolean isActivated) {
        this.isActivated = isActivated;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isActivated);
    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        // This is the player the packet was sent to the server from
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player.containerMenu instanceof CreatorMenu openContainer) {
                openContainer.creator.setActivated(isActivated);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
