package realmayus.youmatter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.replicator.BlockReplicator;
import realmayus.youmatter.replicator.TileReplicator;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class RegistryHandler {

    @SubscribeEvent
    public static void addBlocksAndFluids(RegistryEvent.Register<Block> event) {
        FluidRegistry.registerFluid(ModFluids.UMATTER);
        FluidRegistry.addBucketForFluid(ModFluids.UMATTER);
        FluidRegistry.registerFluid(ModFluids.STABILIZER);
        FluidRegistry.addBucketForFluid(ModFluids.STABILIZER);

        event.getRegistry().register(ModBlocks.UMATTER_BLOCK.setRegistryName(YouMatter.MODID,"umatter_block"));
        event.getRegistry().register(ModBlocks.STABILIZER_BLOCK.setRegistryName(YouMatter.MODID,"stabilizer_block"));
        event.getRegistry().register(new BlockReplicator().setRegistryName(YouMatter.MODID, "replicator").setCreativeTab(YouMatter.creativeTab));

        GameRegistry.registerTileEntity(TileReplicator.class, new ResourceLocation("te_replicator"));
    }

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModItems.UMATTER_BLOCK.setRegistryName(YouMatter.MODID,"umatter_block"));
        event.getRegistry().register(ModItems.STABILIZER_BLOCK.setRegistryName(YouMatter.MODID,"stabilizer_block"));

        event.getRegistry().register(new ItemBlock(ModBlocks.REPLICATOR).setRegistryName(YouMatter.MODID, "replicator"));
        event.getRegistry().register(new ThumbdriveItem().setRegistryName(YouMatter.MODID, "thumb_drive").setCreativeTab(YouMatter.creativeTab));
    }

    /**
     * Will be called by Forge automatically when it's time.
     * Stolen from Cadiboo https://gist.github.com/Cadiboo/3f5cdb785affc069af2fa5fdf2d70358
     */
    @SubscribeEvent
    public static void onRegisterModelsEvent(@Nonnull final ModelRegistryEvent event) {
        ModelLoader.setCustomStateMapper(ModBlocks.UMATTER_BLOCK, new StateMap.Builder().ignore(ModBlocks.UMATTER_BLOCK.LEVEL).build());
        ModelLoader.setCustomStateMapper(ModBlocks.STABILIZER_BLOCK, new StateMap.Builder().ignore(ModBlocks.STABILIZER_BLOCK.LEVEL).build());

        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item.getRegistryName().getNamespace().equals(YouMatter.MODID))
                .forEach(item -> ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "normal")));

//        ModelLoader.setCustomModelResourceLocation(ModItems.UMATTER_BLOCK, 0, new ModelResourceLocation());
    }
}
