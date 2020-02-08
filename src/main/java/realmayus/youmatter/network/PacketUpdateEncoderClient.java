package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import realmayus.youmatter.encoder.IEncoderStateContainer;

public class PacketUpdateEncoderClient implements IMessage {
    public int energy;
    public int progress;

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

        @SideOnly(Side.CLIENT)
        private void handle(PacketUpdateEncoderClient message, MessageContext ctx) {
            ClientPacketHandlers.handlePacketUpdateEncoderClient(message);

        }


    }

}
