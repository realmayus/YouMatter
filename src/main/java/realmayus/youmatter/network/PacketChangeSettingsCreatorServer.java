package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import realmayus.youmatter.creator.ContainerCreator;

public class PacketChangeSettingsCreatorServer implements IMessage {

    private boolean isActivated;

    @Override
    public void fromBytes(ByteBuf buf) {
        isActivated = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isActivated);
    }

    public PacketChangeSettingsCreatorServer() {
    }

    public PacketChangeSettingsCreatorServer(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public static class Handler implements IMessageHandler<PacketChangeSettingsCreatorServer, IMessage> {

        @Override
        public IMessage onMessage(PacketChangeSettingsCreatorServer message, MessageContext ctx) {
            // This is the player the packet was sent to the server from
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                if (serverPlayer.openContainer instanceof ContainerCreator) {
                    ContainerCreator openContainer = (ContainerCreator) serverPlayer.openContainer;
                    openContainer.te.setActivated(message.isActivated);
                }
            });
            // No response packet
            return null;
        }
    }
}
