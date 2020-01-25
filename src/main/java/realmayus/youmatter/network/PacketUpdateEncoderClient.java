package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import realmayus.youmatter.encoder.IEncoderStateContainer;

public class PacketUpdateEncoderClient implements IMessage {
    private int energy;
    private int progress;

    @Override
    public void fromBytes(ByteBuf buf) {
        energy = buf.readInt();
        progress = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(progress);
    }

    public PacketUpdateEncoderClient() {
    }

    public PacketUpdateEncoderClient(int energy, int progress) {
        this.energy = energy;
        this.progress = progress;
    }

    public static class Handler implements IMessageHandler<PacketUpdateEncoderClient, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateEncoderClient message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketUpdateEncoderClient message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            if (player.openContainer instanceof IEncoderStateContainer) {
                ((IEncoderStateContainer) player.openContainer).sync(message.energy, message.progress);
            }
        }


    }

}
