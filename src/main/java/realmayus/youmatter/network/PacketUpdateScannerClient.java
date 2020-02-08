package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import realmayus.youmatter.scanner.IScannerStateContainer;

public class PacketUpdateScannerClient implements IMessage {
    public int energy;
    public int progress;
    public boolean hasEncoder;

    @Override
    public void fromBytes(ByteBuf buf) {
        energy = buf.readInt();
        progress = buf.readInt();
        hasEncoder = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(progress);
        buf.writeBoolean(hasEncoder);
    }

    public PacketUpdateScannerClient() {
    }

    public PacketUpdateScannerClient(int energy, int progress, boolean hasEncoder) {
        this.energy = energy;
        this.progress = progress;
        this.hasEncoder = hasEncoder;
    }

    public static class Handler implements IMessageHandler<PacketUpdateScannerClient, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateScannerClient message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handle(PacketUpdateScannerClient message, MessageContext ctx) {
            ClientPacketHandlers.handlePacketUpdateScannerClient(message);
        }
    }
}
