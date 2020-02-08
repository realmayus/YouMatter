package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import realmayus.youmatter.creator.ICreatorStateContainer;

public class PacketUpdateCreatorClient implements IMessage {
    public int uFluidAmount;
    public int sFluidAmount;
    public int energy;
    public int progress;
    public NBTTagCompound uTank;
    public NBTTagCompound sTank;
    public boolean isActivated;

    @Override
    public void fromBytes(ByteBuf buf) {
        uFluidAmount = buf.readInt();
        sFluidAmount = buf.readInt();
        energy = buf.readInt();
        progress = buf.readInt();
        uTank = ByteBufUtils.readTag(buf);
        sTank = ByteBufUtils.readTag(buf);
        isActivated = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(uFluidAmount);
        buf.writeInt(sFluidAmount);
        buf.writeInt(energy);
        buf.writeInt(progress);
        ByteBufUtils.writeTag(buf, uTank);
        ByteBufUtils.writeTag(buf, sTank);
        buf.writeBoolean(isActivated);
    }

    public PacketUpdateCreatorClient() {
    }

    public PacketUpdateCreatorClient(int uFluidAmount, int sFluidAmount, int energy, int progress, NBTTagCompound uTank, NBTTagCompound sTank, boolean isActivated) {
        this.uFluidAmount = uFluidAmount;
        this.sFluidAmount = sFluidAmount;
        this.energy = energy;
        this.progress = progress;
        this.uTank = uTank;
        this.sTank = sTank;
        this.isActivated = isActivated;
    }

    public static class Handler implements IMessageHandler<PacketUpdateCreatorClient, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateCreatorClient message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handle(PacketUpdateCreatorClient message, MessageContext ctx) {
            ClientPacketHandlers.handlePacketUpdateCreatorClient(message);


        }


    }

}
