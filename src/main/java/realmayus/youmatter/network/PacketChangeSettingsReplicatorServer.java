package realmayus.youmatter.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import realmayus.youmatter.replicator.ReplicatorMenu;
//import realmayus.youmatter.replicator.ContainerReplicator;

public class PacketChangeSettingsReplicatorServer {

    private boolean isActivated;
    private boolean mode;

    public PacketChangeSettingsReplicatorServer(FriendlyByteBuf buf) {
        isActivated = buf.readBoolean();
        mode = buf.readBoolean();
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isActivated);
        buf.writeBoolean(mode);
    }

    public PacketChangeSettingsReplicatorServer(boolean isActivated, boolean mode) {
        this.isActivated = isActivated;
        this.mode = mode;
    }


    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player.containerMenu instanceof ReplicatorMenu openContainer) {
                openContainer.replicator.setActive(isActivated);
                openContainer.replicator.setCurrentMode(mode);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
