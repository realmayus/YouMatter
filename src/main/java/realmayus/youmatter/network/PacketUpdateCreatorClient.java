package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import realmayus.youmatter.creator.ICreatorStateContainer;

public class PacketUpdateCreatorClient implements IMessage {
    private int uFluidAmount;
    private int sFluidAmount;
    private int energy;
    private int progress;
    private NBTTagCompound uTank;
    private NBTTagCompound sTank;

    @Override
    public void fromBytes(ByteBuf buf) {
        uFluidAmount = buf.readInt();
        sFluidAmount = buf.readInt();
        energy = buf.readInt();
        progress = buf.readInt();
        uTank = ByteBufUtils.readTag(buf);
        sTank = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(uFluidAmount);
        buf.writeInt(sFluidAmount);
        buf.writeInt(energy);
        buf.writeInt(progress);
        ByteBufUtils.writeTag(buf, uTank);
        ByteBufUtils.writeTag(buf, sTank);
    }

    public PacketUpdateCreatorClient() {
    }

    public PacketUpdateCreatorClient(int uFluidAmount, int sFluidAmount, int energy, int progress, NBTTagCompound uTank, NBTTagCompound sTank) {
        this.uFluidAmount = uFluidAmount;
        this.sFluidAmount = sFluidAmount;
        this.energy = energy;
        this.progress = progress;
        this.uTank = uTank;
        this.sTank = sTank;
    }

    public static class Handler implements IMessageHandler<PacketUpdateCreatorClient, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateCreatorClient message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketUpdateCreatorClient message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            if (player.openContainer instanceof ICreatorStateContainer) {
                ((ICreatorStateContainer) player.openContainer).sync(message.uFluidAmount, message.sFluidAmount, message.energy, message.progress, message.uTank, message.sTank);
            }
        }


    }

}
