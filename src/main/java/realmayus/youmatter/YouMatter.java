package realmayus.youmatter;

import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.util.GuiHandler;

@Mod(modid = YouMatter.MODID, name = YouMatter.NAME, version = YouMatter.VERSION)
public class YouMatter
{
    public static final String MODID = "youmatter";
    public static final String NAME = "You Matter";
    public static final String VERSION = "1.0";

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
            return new ItemStack(ModBlocks.REPLICATOR);
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
    }
}
