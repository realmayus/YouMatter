package realmayus.youmatter;


import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import realmayus.youmatter.network.PacketHandler;

@Mod(YouMatter.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class YouMatter {
    public static final String MODID = "youmatter";

    public static final Logger logger = LogManager.getLogger();


    public YouMatter() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, YMConfig.CONFIG_SPEC);
        ModFluids.init();
    }

    public static CreativeModeTab ITEM_GROUP = new CreativeModeTab("YouMatter") { //todo localize
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ObjectHolders.SCANNER_BLOCK, 1);
        }
    };

    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

}
