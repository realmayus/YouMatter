package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import realmayus.youmatter.replicator.ContainerReplicator;

public class PacketShowNext implements IMessage{

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public PacketShowNext() {
    }

    public static class Handler implements IMessageHandler<PacketShowNext, IMessage> {

        @Override
        public IMessage onMessage(PacketShowNext message, MessageContext ctx) {
            // This is the player the packet was sent to the server from
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                if (serverPlayer.openContainer instanceof ContainerReplicator) {
                    ContainerReplicator openContainer = (ContainerReplicator) serverPlayer.openContainer;
                    openContainer.te.renderNext();
                }
            });
            // No response packet
            return null;
        }
    }

}
