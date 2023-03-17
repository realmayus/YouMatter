package realmayus.youmatter;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import realmayus.youmatter.creator.CreatorBlock;
import realmayus.youmatter.creator.CreatorBlockEntity;
import realmayus.youmatter.creator.CreatorMenu;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderBlockEntity;
import realmayus.youmatter.encoder.EncoderMenu;
import realmayus.youmatter.fluid.StabilizerFluidBlock;
import realmayus.youmatter.fluid.StabilizerFluidType;
import realmayus.youmatter.fluid.UMatterFluidType;
import realmayus.youmatter.items.BlackHoleItem;
import realmayus.youmatter.items.ComputeModuleItem;
import realmayus.youmatter.items.MachineCasingItem;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.items.TransistorItem;
import realmayus.youmatter.items.TransistorRawItem;
import realmayus.youmatter.replicator.ReplicatorBlock;
import realmayus.youmatter.replicator.ReplicatorBlockEntity;
import realmayus.youmatter.replicator.ReplicatorMenu;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerBlockEntity;
import realmayus.youmatter.scanner.ScannerMenu;

public class ModContent {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, YouMatter.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, YouMatter.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, YouMatter.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, YouMatter.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, YouMatter.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, YouMatter.MODID);

    public static final RegistryObject<ScannerBlock> SCANNER_BLOCK = BLOCKS.register("scanner", () -> new ScannerBlock());
    public static final RegistryObject<MenuType<ScannerMenu>> SCANNER_MENU = MENU_TYPES.register("scanner", () -> IForgeMenuType.create((windowId, inv, data) -> new ScannerMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
    public static final RegistryObject<BlockEntityType<ScannerBlockEntity>> SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner", () -> BlockEntityType.Builder.of(ScannerBlockEntity::new, SCANNER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockItem> SCANNER_BLOCK_ITEM = ITEMS.register("scanner", () -> new BlockItem(SCANNER_BLOCK.get(), new Item.Properties()));
   
    public static final RegistryObject<EncoderBlock> ENCODER_BLOCK = BLOCKS.register("encoder", () -> new EncoderBlock());
    public static final RegistryObject<MenuType<EncoderMenu>> ENCODER_MENU = MENU_TYPES.register("encoder", () -> IForgeMenuType.create((windowId, inv, data) -> new EncoderMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
    public static final RegistryObject<BlockEntityType<EncoderBlockEntity>> ENCODER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("encoder", () -> BlockEntityType.Builder.of(EncoderBlockEntity::new, ENCODER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockItem> ENCODER_BLOCK_ITEM = ITEMS.register("encoder", () -> new BlockItem(ENCODER_BLOCK.get(), new Item.Properties()));
    
    public static final RegistryObject<CreatorBlock> CREATOR_BLOCK = BLOCKS.register("creator", () -> new CreatorBlock());
    public static final RegistryObject<MenuType<CreatorMenu>> CREATOR_MENU = MENU_TYPES.register("creator", () -> IForgeMenuType.create((windowId, inv, data) -> new CreatorMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
    public static final RegistryObject<BlockEntityType<CreatorBlockEntity>> CREATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("creator", () -> BlockEntityType.Builder.of(CreatorBlockEntity::new, CREATOR_BLOCK.get()).build(null));
    public static final RegistryObject<BlockItem> CREATOR_BLOCK_ITEM = ITEMS.register("creator", () -> new BlockItem(CREATOR_BLOCK.get(), new Item.Properties()));
    
    public static final RegistryObject<ReplicatorBlock> REPLICATOR_BLOCK = BLOCKS.register("replicator", () -> new ReplicatorBlock());
    public static final RegistryObject<MenuType<ReplicatorMenu>> REPLICATOR_MENU = MENU_TYPES.register("replicator", () -> IForgeMenuType.create((windowId, inv, data) -> new ReplicatorMenu(windowId, inv.player.level, data.readBlockPos(), inv, inv.player)));
    public static final RegistryObject<BlockEntityType<ReplicatorBlockEntity>> REPLICATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("replicator", () -> BlockEntityType.Builder.of(ReplicatorBlockEntity::new, REPLICATOR_BLOCK.get()).build(null));
    public static final RegistryObject<BlockItem> REPLICATOR_BLOCK_ITEM = ITEMS.register("replicator", () -> new BlockItem(REPLICATOR_BLOCK.get(), new Item.Properties()));
    
    public static final RegistryObject<FluidType> STABILIZER_TYPE = FLUID_TYPES.register("stabilizer", () -> new StabilizerFluidType());
    public static final RegistryObject<FlowingFluid> STABILIZER = FLUIDS.register("stabilizer", () -> new ForgeFlowingFluid.Source(ModContent.STABILIZER_PROPERIES));
    public static final RegistryObject<FlowingFluid> STABILIZER_FLOWING = FLUIDS.register("stabilizer_flowing", () -> new ForgeFlowingFluid.Flowing(ModContent.STABILIZER_PROPERIES));
    public static final RegistryObject<StabilizerFluidBlock> STABILIZER_FLUID_BLOCK = BLOCKS.register("stabilizer_fluid_block", () -> new StabilizerFluidBlock(STABILIZER, BlockBehaviour.Properties.of(Material.LAVA).noCollission().strength(1.0F).noLootTable()));
    public static final RegistryObject<Item> STABILIZER_BUCKET = ITEMS.register("stabilizer_bucket", () -> new BucketItem(STABILIZER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final ForgeFlowingFluid.Properties STABILIZER_PROPERIES = new ForgeFlowingFluid.Properties(STABILIZER_TYPE, STABILIZER, STABILIZER_FLOWING).bucket(STABILIZER_BUCKET).block(STABILIZER_FLUID_BLOCK);

    public static final RegistryObject<FluidType> UMATTER_TYPE = FLUID_TYPES.register("umatter", () -> new UMatterFluidType());
    public static final RegistryObject<FlowingFluid> UMATTER = FLUIDS.register("umatter", () -> new ForgeFlowingFluid.Source(ModContent.UMATTER_PROPERTIES));
    public static final RegistryObject<FlowingFluid> UMATTER_FLOWING = FLUIDS.register("umatter_flowing", () -> new ForgeFlowingFluid.Flowing(ModContent.UMATTER_PROPERTIES));
    public static final RegistryObject<StabilizerFluidBlock> UMATTER_FLUID_BLOCK = BLOCKS.register("umatter_fluid_block", () -> new StabilizerFluidBlock(UMATTER, BlockBehaviour.Properties.of(Material.LAVA).noCollission().strength(1.0F).noLootTable()));
    public static final RegistryObject<Item> UMATTER_BUCKET = ITEMS.register("umatter_bucket", () -> new BucketItem(UMATTER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final ForgeFlowingFluid.Properties UMATTER_PROPERTIES = new ForgeFlowingFluid.Properties(UMATTER_TYPE, UMATTER, UMATTER_FLOWING).bucket(UMATTER_BUCKET).block(UMATTER_FLUID_BLOCK);

    public static final RegistryObject<BlackHoleItem> BLACK_HOLE_ITEM = ITEMS.register("black_hole", () -> new BlackHoleItem());
    public static final RegistryObject<ThumbdriveItem> THUMBDRIVE_ITEM = ITEMS.register("thumb_drive", () -> new ThumbdriveItem());
    public static final RegistryObject<MachineCasingItem> MACHINE_CASING_ITEM = ITEMS.register("machine_casing", () -> new MachineCasingItem());
    public static final RegistryObject<ComputeModuleItem> COMPUTE_MODULE_ITEM = ITEMS.register("compute_module", () -> new ComputeModuleItem());
    public static final RegistryObject<TransistorItem> TRANSISTOR_ITEM = ITEMS.register("transistor", () -> new TransistorItem());
    public static final RegistryObject<TransistorRawItem> TRANSISTOR_RAW_ITEM = ITEMS.register("transistor_raw", () -> new TransistorRawItem());
    
    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
    }
}
