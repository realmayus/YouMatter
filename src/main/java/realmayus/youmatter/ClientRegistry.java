package realmayus.youmatter;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import realmayus.youmatter.scanner.ScannerScreen;
import realmayus.youmatter.util.GuiHandler;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = YouMatter.MODID, value = Dist.CLIENT)
public class ClientRegistry {
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(ObjectHolders.SCANNER_CONTAINER, ScannerScreen::new);
    }
}
