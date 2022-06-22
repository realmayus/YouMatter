package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
//import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.network.NetworkEvent;
import realmayus.youmatter.replicator.ReplicatorContainer;
//import realmayus.youmatter.replicator.ContainerReplicator;

import java.util.function.Supplier;

public class PacketChangeSettingsReplicatorServer {

    private boolean isActivated;
    private boolean mode;

    public PacketChangeSettingsReplicatorServer(PacketBuffer buf) {
        isActivated = buf.readBoolean();
        mode = buf.readBoolean();
    }

    void encode(PacketBuffer buf) {
        buf.writeBoolean(isActivated);
        buf.writeBoolean(mode);
    }

    public PacketChangeSettingsReplicatorServer(boolean isActivated, boolean mode) {
        this.isActivated = isActivated;
        this.mode = mode;
    }


    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player.containerMenu instanceof ReplicatorContainer) {
                ReplicatorContainer openContainer = (ReplicatorContainer) player.containerMenu;
                openContainer.te.setActive(isActivated);
                openContainer.te.setCurrentMode(mode);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
