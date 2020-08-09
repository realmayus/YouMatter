package realmayus.youmatter;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.util.GuiHandler;
import realmayus.youmatter.util.LootHandler;


@Mod(modid = YouMatter.MODID, name = YouMatter.NAME, version = YouMatter.VERSION)
public class YouMatter
{
    public static final String MODID = "youmatter";
    public static final String NAME = "You Matter";
    public static final String VERSION = "1.12.2-1.8";
    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }
    static {
        FluidRegistry.enableUniversalBucket();
    }


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

    @Mod.Instance
    public static YouMatter instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        PacketHandler.registerMessages();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(YouMatter.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new LootHandler());
        LootTableList.register(new ResourceLocation(YouMatter.MODID, "inject/end_city_treasure"));
    }


}
