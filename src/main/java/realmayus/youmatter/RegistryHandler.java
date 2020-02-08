package realmayus.youmatter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import realmayus.youmatter.creator.BlockCreator;
import realmayus.youmatter.creator.TileCreator;
import realmayus.youmatter.encoder.BlockEncoder;
import realmayus.youmatter.encoder.TileEncoder;
import realmayus.youmatter.items.*;
import realmayus.youmatter.replicator.BlockReplicator;
import realmayus.youmatter.replicator.TileReplicator;
import realmayus.youmatter.scanner.BlockScanner;
import realmayus.youmatter.scanner.TileScanner;

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
        event.getRegistry().register(new BlockScanner().setRegistryName(YouMatter.MODID, "scanner").setCreativeTab(YouMatter.creativeTab));
        event.getRegistry().register(new BlockEncoder().setRegistryName(YouMatter.MODID, "encoder").setCreativeTab(YouMatter.creativeTab));
        event.getRegistry().register(new BlockCreator().setRegistryName(YouMatter.MODID, "creator").setCreativeTab(YouMatter.creativeTab));

        GameRegistry.registerTileEntity(TileReplicator.class, new ResourceLocation("youmatter","te_replicator"));
        GameRegistry.registerTileEntity(TileCreator.class, new ResourceLocation("youmatter", "te_creator"));
        GameRegistry.registerTileEntity(TileScanner.class, new ResourceLocation("youmatter", "te_scanner"));
        GameRegistry.registerTileEntity(TileEncoder.class, new ResourceLocation("youmatter", "te_encoder"));
    }


    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModItems.UMATTER_BLOCK.setRegistryName(YouMatter.MODID,"umatter_block"));
        event.getRegistry().register(ModItems.STABILIZER_BLOCK.setRegistryName(YouMatter.MODID,"stabilizer_block"));

        event.getRegistry().register(new ItemBlock(ModBlocks.REPLICATOR).setRegistryName(YouMatter.MODID, "replicator"));
        event.getRegistry().register(new ItemBlock(ModBlocks.SCANNER).setRegistryName(YouMatter.MODID, "scanner"));
        event.getRegistry().register(new ItemBlock(ModBlocks.ENCODER).setRegistryName(YouMatter.MODID, "encoder"));
        event.getRegistry().register(new ItemBlock(ModBlocks.CREATOR).setRegistryName(YouMatter.MODID, "creator"));
        event.getRegistry().register(new ThumbdriveItem().setRegistryName(YouMatter.MODID, "thumb_drive").setCreativeTab(YouMatter.creativeTab));
        event.getRegistry().register(new ComputeModuleItem().setRegistryName(YouMatter.MODID, "compute_module").setCreativeTab(YouMatter.creativeTab));
        event.getRegistry().register(new TransistorItem().setRegistryName(YouMatter.MODID, "transistor").setCreativeTab(YouMatter.creativeTab));
        event.getRegistry().register(new TransistorRawItem().setRegistryName(YouMatter.MODID, "transistor_raw").setCreativeTab(YouMatter.creativeTab));
        event.getRegistry().register(new MachineCasingItem().setRegistryName(YouMatter.MODID, "machine_casing").setCreativeTab(YouMatter.creativeTab));
        event.getRegistry().register(new BlackHoleItem().setRegistryName(YouMatter.MODID, "black_hole").setCreativeTab(YouMatter.creativeTab));
    }

    @SubscribeEvent
    public static void addRecipes(RegistryEvent.Register<IRecipe> event) {
        GameRegistry.addSmelting(ModItems.TRANSISTOR_RAW, new ItemStack(ModItems.TRANSISTOR, 1), 1.5f);
    }
}
