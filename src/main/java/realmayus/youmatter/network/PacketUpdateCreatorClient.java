package realmayus.youmatter.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateCreatorClient {
    public int uFluidAmount;
    public int sFluidAmount;
    public int energy;
    public int progress;
    public FluidStack uTank;
    public FluidStack sTank;
    public boolean isActivated;

    public void encode(PacketBuffer buf) {
        buf.writeInt(uFluidAmount);
        buf.writeInt(sFluidAmount);
        buf.writeInt(energy);
        buf.writeInt(progress);
        buf.writeFluidStack(uTank);
        buf.writeFluidStack(sTank);
        buf.writeBoolean(isActivated);
    }

    public PacketUpdateCreatorClient(PacketBuffer buf) {
        uFluidAmount = buf.readInt();
        sFluidAmount = buf.readInt();
        energy = buf.readInt();
        progress = buf.readInt();
        uTank = buf.readFluidStack();
        sTank = buf.readFluidStack();
        isActivated = buf.readBoolean();
    }

    public PacketUpdateCreatorClient(int uFluidAmount, int sFluidAmount, int energy, int progress, FluidStack uTank, FluidStack sTank, boolean isActivated) {
        this.uFluidAmount = uFluidAmount;
        this.sFluidAmount = sFluidAmount;
        this.energy = energy;
        this.progress = progress;
        this.uTank = uTank;
        this.sTank = sTank;
        this.isActivated = isActivated;
    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandlers.handlePacketUpdateCreatorClient(this);
        });
        ctx.get().setPacketHandled(true);
    }
}