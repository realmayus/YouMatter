package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import realmayus.youmatter.replicator.ContainerReplicator;

public class PacketChangeSettingsReplicatorServer implements IMessage {

    private boolean isActivated;
    private boolean mode;

    @Override
    public void fromBytes(ByteBuf buf) {
        isActivated = buf.readBoolean();
        mode = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isActivated);
        buf.writeBoolean(mode);
    }

    public PacketChangeSettingsReplicatorServer() {
    }

    public PacketChangeSettingsReplicatorServer(boolean isActivated, boolean mode) {
        this.isActivated = isActivated;
        this.mode = mode;
    }

    public static class Handler implements IMessageHandler<PacketChangeSettingsReplicatorServer, IMessage> {

        @Override
        public IMessage onMessage(PacketChangeSettingsReplicatorServer message, MessageContext ctx) {
            // This is the player the packet was sent to the server from
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                if (serverPlayer.openContainer instanceof ContainerReplicator) {
                    ContainerReplicator openContainer = (ContainerReplicator) serverPlayer.openContainer;
                    openContainer.te.setActive(message.isActivated);
                    openContainer.te.setCurrentMode(message.mode);
                }
            });
            // No response packet
            return null;
        }
    }
}
