package realmayus.youmatter.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketUpdateReplicatorClient {
    public int energy;
    public int progress;
    public FluidStack fluidStack;
    public boolean isActivated;
    public boolean mode;


    public PacketUpdateReplicatorClient(FriendlyByteBuf buf) {
        energy = buf.readInt();
        progress = buf.readInt();
        isActivated = buf.readBoolean();
        mode = buf.readBoolean();
        fluidStack = buf.readFluidStack();
    }

    public PacketUpdateReplicatorClient(Integer energy, Integer progress, Boolean isActivated, Boolean mode, FluidStack fluidStack) {
        this.energy = energy;
        this.progress = progress;
        this.isActivated = isActivated;
        this.mode = mode;
        this.fluidStack = fluidStack;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(progress);
        buf.writeBoolean(isActivated);
        buf.writeBoolean(mode);
        buf.writeFluidStack(fluidStack);
    }

    void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandlers.handlePacketUpdateReplicatorClient(this);
        });
        ctx.get().setPacketHandled(true);
    }
}
