package realmayus.youmatter.network;

import javafx.geometry.Side;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import realmayus.youmatter.YouMatter;

public class PacketHandler {
    public static SimpleChannel INSTANCE;

    private static int ID = 0;
    private static int nextID() {return ID++;}

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(YouMatter.MODID, "youmatter"), () -> "1.0", s -> true, s -> true);
        INSTANCE.registerMessage(nextID(), PacketUpdateScannerClient.class, PacketUpdateScannerClient::encode, PacketUpdateScannerClient::new, PacketUpdateScannerClient::handle);
        INSTANCE.registerMessage(nextID(), PacketUpdateReplicatorClient.class, PacketUpdateReplicatorClient::encode, PacketUpdateReplicatorClient::new, PacketUpdateReplicatorClient::handle);
        INSTANCE.registerMessage(nextID(), PacketUpdateCreatorClient.class, PacketUpdateCreatorClient::encode, PacketUpdateCreatorClient::new, PacketUpdateCreatorClient::handle);
        INSTANCE.registerMessage(nextID(), PacketUpdateEncoderClient.class, PacketUpdateEncoderClient::encode, PacketUpdateEncoderClient::new, PacketUpdateEncoderClient::handle);


        INSTANCE.registerMessage(PacketShowPrevious.Handler.class, PacketShowPrevious.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketShowNext.Handler.class, PacketShowNext.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketChangeSettingsReplicatorServer.Handler.class, PacketChangeSettingsReplicatorServer.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketChangeSettingsCreatorServer.Handler.class, PacketChangeSettingsCreatorServer.class, nextID(), Side.SERVER);

    }
}
