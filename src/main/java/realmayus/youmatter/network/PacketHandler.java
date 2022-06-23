package realmayus.youmatter.network;

import org.apache.logging.log4j.Level;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import realmayus.youmatter.YouMatter;

public class PacketHandler {
    public static SimpleChannel INSTANCE;

    private static int ID = 0;
    private static int nextID() {return ID++;}

    public static void registerMessages() {
        YouMatter.logger.log(Level.INFO, "Registered Packets");
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(YouMatter.MODID, "youmatter"), () -> "1.0", s -> true, s -> true);

        //To: Client
        //(nothing here)

        //To: Server
        INSTANCE.registerMessage(nextID(), PacketShowNext.class, PacketShowNext::encode, PacketShowNext::new, PacketShowNext::handle);
        INSTANCE.registerMessage(nextID(), PacketShowPrevious.class, PacketShowPrevious::encode, PacketShowPrevious::new, PacketShowPrevious::handle);
        INSTANCE.registerMessage(nextID(), PacketChangeSettingsReplicatorServer.class, PacketChangeSettingsReplicatorServer::encode, PacketChangeSettingsReplicatorServer::new, PacketChangeSettingsReplicatorServer::handle);
        INSTANCE.registerMessage(nextID(), PacketChangeSettingsCreatorServer.class, PacketChangeSettingsCreatorServer::encode, PacketChangeSettingsCreatorServer::new, PacketChangeSettingsCreatorServer::handle);

    }
}
