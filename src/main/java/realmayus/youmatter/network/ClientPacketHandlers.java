package realmayus.youmatter.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import realmayus.youmatter.creator.ICreatorStateContainer;
import realmayus.youmatter.encoder.IEncoderStateContainer;
import realmayus.youmatter.replicator.IReplicatorStateContainer;
import realmayus.youmatter.scanner.IScannerStateContainer;

public class ClientPacketHandlers {
    public static void handlePacketUpdateScannerClient(PacketUpdateScannerClient message) {
        PlayerEntity player = Minecraft.getInstance().player;

        if (player.openContainer instanceof IScannerStateContainer) {
            ((IScannerStateContainer) player.openContainer).sync(message.energy, message.progress, message.hasEncoder);
        }
    }

    public static void handlePacketUpdateCreatorClient(PacketUpdateCreatorClient message) {
        PlayerEntity player = Minecraft.getInstance().player;

        if (player.openContainer instanceof ICreatorStateContainer) {
            ((ICreatorStateContainer) player.openContainer).sync(message.uFluidAmount, message.sFluidAmount, message.energy, message.progress, message.uTank, message.sTank, message.isActivated);
        }
    }

    public static void handlePacketUpdateEncoderClient(PacketUpdateEncoderClient message) {

        PlayerEntity player = Minecraft.getInstance().player;

        if (player.openContainer instanceof IEncoderStateContainer) {
            ((IEncoderStateContainer) player.openContainer).sync(message.energy, message.progress);
        }
    }

    public static void handlePacketUpdateReplicatorClient(PacketUpdateReplicatorClient message) {
        PlayerEntity player = Minecraft.getInstance().player;

        if (player.openContainer instanceof IReplicatorStateContainer) {
            ((IReplicatorStateContainer) player.openContainer).sync(message.fluidAmount, message.energy, message.progress, message.tank, message.isActivated, message.mode);
        }
    }
}
