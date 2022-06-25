package realmayus.youmatter;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import realmayus.youmatter.creator.CreatorScreen;
import realmayus.youmatter.encoder.EncoderScreen;
import realmayus.youmatter.replicator.ReplicatorScreen;
import realmayus.youmatter.scanner.ScannerScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = YouMatter.MODID, value = Dist.CLIENT)
public class ClientRegistry {
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event)
    {
        MenuScreens.register(ObjectHolders.SCANNER_MENU, ScannerScreen::new);
        MenuScreens.register(ObjectHolders.ENCODER_MENU, EncoderScreen::new);
        MenuScreens.register(ObjectHolders.CREATOR_MENU, CreatorScreen::new);
        MenuScreens.register(ObjectHolders.REPLICATOR_MENU, ReplicatorScreen::new);
    }
}
