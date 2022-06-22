package realmayus.youmatter.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
//import net.minecraft.entity.player.PlayerEntity;
//import realmayus.youmatter.creator.ICreatorStateContainer;
//import realmayus.youmatter.encoder.IEncoderStateContainer;
//import realmayus.youmatter.replicator.IReplicatorStateContainer;
import realmayus.youmatter.creator.ICreatorStateContainer;
import realmayus.youmatter.encoder.IEncoderStateContainer;
import realmayus.youmatter.replicator.IReplicatorStateContainer;
import realmayus.youmatter.scanner.IScannerStateContainer;

public class ClientPacketHandlers {
    public static void handlePacketUpdateScannerClient(PacketUpdateScannerClient message) {
        Player player = Minecraft.getInstance().player;

        if (player.containerMenu instanceof IScannerStateContainer) {
            ((IScannerStateContainer) player.containerMenu).sync(message.energy, message.progress, message.hasEncoder);
        }
    }

    public static void handlePacketUpdateCreatorClient(PacketUpdateCreatorClient message) {
        Player player = Minecraft.getInstance().player;

        if (player.containerMenu instanceof ICreatorStateContainer) {
            ((ICreatorStateContainer) player.containerMenu).sync(message.energy, message.progress, message.uTank, message.sTank, message.isActivated);
        }
    }

    public static void handlePacketUpdateEncoderClient(PacketUpdateEncoderClient message) {

        Player player = Minecraft.getInstance().player;

        if (player.containerMenu instanceof IEncoderStateContainer) {
            ((IEncoderStateContainer) player.containerMenu).sync(message.energy, message.progress);
        }
    }

    public static void handlePacketUpdateReplicatorClient(PacketUpdateReplicatorClient message) {
        Player player = Minecraft.getInstance().player;

        if (player.containerMenu instanceof IReplicatorStateContainer) {
            ((IReplicatorStateContainer) player.containerMenu).sync(message.energy, message.progress, message.fluidStack, message.isActivated, message.mode);
        }
    }
}
