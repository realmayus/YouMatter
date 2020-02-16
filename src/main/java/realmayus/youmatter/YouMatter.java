package realmayus.youmatter;


import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import realmayus.youmatter.network.PacketHandler;

@Mod(YouMatter.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class YouMatter {
    public static final String MODID = "youmatter";

    public static final Logger logger = LogManager.getLogger();

//    static {
//        FluidRegistry.enableUniversalBucket();
//    }
/*
    public static ItemTab creativeTab = new CreativeTabs("youmatter") {
        @Override
        public ItemStack createIcon() {
            return FluidUtil.getFilledBucket(new FluidStack(ModFluids.UMATTER, 1));
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> items) {
            super.displayAllRelevantItems(items);
            items.add(FluidUtil.getFilledBucket(new FluidStack(ModFluids.UMATTER, 1)));
            items.add(FluidUtil.getFilledBucket(new FluidStack(ModFluids.STABILIZER, 1)));
        }
    };*/

    public YouMatter() {
        ModFluids.init();
    }

    public static ItemGroup ITEM_GROUP = new ItemGroup("YouMatter") { //todo localize
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ObjectHolders.SCANNER_BLOCK, 1);
        }
    };

    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

}
