package realmayus.youmatter;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import realmayus.youmatter.network.PacketHandler;

@Mod(YouMatter.MODID)
@Mod.EventBusSubscriber(bus= Bus.MOD)
public class YouMatter
{
    public static final String MODID = "youmatter";

    private static final Logger logger = LogManager.getLogger();

    //    static {
//        FluidRegistry.enableUniversalBucket();
//    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup("mytutorial") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ObjectHolders.SCANNER); //todo change
        }
    };

    @SubscribeEvent
    public void init(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }
}
