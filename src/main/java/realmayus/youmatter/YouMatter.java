package realmayus.youmatter;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.util.GuiHandler;
import realmayus.youmatter.util.LootHandler;

@Mod(YouMatter.MODID)
@Mod.EventBusSubscriber(bus= Bus.MOD)
public class YouMatter
{
    public static final String MODID = "youmatter";

    private static final Logger logger = LogManager.getLogger();

//    static {
//        FluidRegistry.enableUniversalBucket();
//    }

    public static CreativeTabs creativeTab = new CreativeTabs("youmatter") {
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
    };

    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

    @SubscribeEvent
    public void init(FMLInitializationEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus()ExtensionPoint.GUIFACTORY, ...)        MinecraftForge.EVENT_BUS.register(new LootHandler());
        LootTableList.register(new ResourceLocation(YouMatter.MODID, "inject/end_city_treasure"));
    }
}
