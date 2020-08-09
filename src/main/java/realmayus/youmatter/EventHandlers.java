package realmayus.youmatter;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandlers {

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e) {
        System.out.println("test1");
        if(e.getModID().equalsIgnoreCase(YouMatter.MODID)) {
            System.out.println("test2");
            ConfigManager.sync(YouMatter.MODID, Config.Type.INSTANCE);
        }
    }

}
