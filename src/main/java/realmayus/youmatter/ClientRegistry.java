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
        event.enqueueWork(() -> {
            MenuScreens.register(ModContent.SCANNER_MENU.get(), ScannerScreen::new);
            MenuScreens.register(ModContent.ENCODER_MENU.get(), EncoderScreen::new);
            MenuScreens.register(ModContent.CREATOR_MENU.get(), CreatorScreen::new);
            MenuScreens.register(ModContent.REPLICATOR_MENU.get(), ReplicatorScreen::new);
        });
    }
}
