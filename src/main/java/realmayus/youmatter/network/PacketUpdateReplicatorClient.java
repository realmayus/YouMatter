package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import realmayus.youmatter.replicator.IReplicatorStateContainer;

public class PacketUpdateReplicatorClient implements IMessage {
    private int fluidAmount;
    private int energy;
    private int progress;
    private NBTTagCompound tank;
    private boolean isActivated;
    private boolean mode;

    @Override
    public void fromBytes(ByteBuf buf) {
        fluidAmount = buf.readInt();
        energy = buf.readInt();
        progress = buf.readInt();
        tank = ByteBufUtils.readTag(buf);
        isActivated = buf.readBoolean();
        mode = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fluidAmount);
        buf.writeInt(energy);
        buf.writeInt(progress);
        ByteBufUtils.writeTag(buf, tank);
        buf.writeBoolean(isActivated);
        buf.writeBoolean(mode);
    }

    public PacketUpdateReplicatorClient() {
    }

    public PacketUpdateReplicatorClient(int fluidAmount, int energy, int progress, NBTTagCompound tank, boolean isActivated, boolean mode) {
        this.fluidAmount = fluidAmount;
        this.energy = energy;
        this.progress = progress;
        this.tank = tank;
        this.isActivated = isActivated;
        this.mode = mode;
    }

    public static class Handler implements IMessageHandler<PacketUpdateReplicatorClient, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateReplicatorClient message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketUpdateReplicatorClient message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            if (player.openContainer instanceof IReplicatorStateContainer) {
                ((IReplicatorStateContainer) player.openContainer).sync(message.fluidAmount, message.energy, message.progress, message.tank, message.isActivated, message.mode);
            }
        }


    }

}
