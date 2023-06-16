package realmayus.youmatter;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import realmayus.youmatter.network.PacketHandler;

@Mod(YouMatter.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class YouMatter {
    public static final String MODID = "youmatter";
    public static final Logger logger = LogManager.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(ModContent.SCANNER_BLOCK.get()))
            .title(Component.literal("YouMatter")) //todo localize
            .displayItems((displayParameters, output) -> {
                output.acceptAll(List.of(
                        new ItemStack(ModContent.SCANNER_BLOCK.get()),
                        new ItemStack(ModContent.ENCODER_BLOCK.get()),
                        new ItemStack(ModContent.CREATOR_BLOCK.get()),
                        new ItemStack(ModContent.REPLICATOR_BLOCK.get()),
                        new ItemStack(ModContent.MACHINE_CASING_ITEM.get()),
                        new ItemStack(ModContent.BLACK_HOLE_ITEM.get()),
                        new ItemStack(ModContent.COMPUTE_MODULE_ITEM.get()),
                        new ItemStack(ModContent.TRANSISTOR_RAW_ITEM.get()),
                        new ItemStack(ModContent.TRANSISTOR_ITEM.get()),
                        new ItemStack(ModContent.THUMBDRIVE_ITEM.get()),
                        new ItemStack(ModContent.UMATTER_BUCKET.get()),
                        new ItemStack(ModContent.STABILIZER_BUCKET.get())));
            }).build());

    public YouMatter() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, YMConfig.CONFIG_SPEC);
        ModContent.init(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

    }

    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

}
