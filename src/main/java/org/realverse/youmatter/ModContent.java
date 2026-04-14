package org.realverse.youmatter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.realverse.youmatter.creator.CreatorBlock;
import org.realverse.youmatter.creator.CreatorBlockEntity;
import org.realverse.youmatter.creator.CreatorMenu;
import org.realverse.youmatter.creator.old.CreatorBlockOld;
import org.realverse.youmatter.creator.old.CreatorBlockEntityOld;
import org.realverse.youmatter.creator.old.CreatorMenuOld;
import org.realverse.youmatter.encoder.EncoderBlock;
import org.realverse.youmatter.encoder.EncoderBlockEntity;
import org.realverse.youmatter.encoder.EncoderMenu;
import org.realverse.youmatter.fluid.StabilizerFluidBlock;
import org.realverse.youmatter.fluid.StabilizerFluidType;
import org.realverse.youmatter.fluid.UMatterFluidBlock;
import org.realverse.youmatter.fluid.UMatterFluidType;
import org.realverse.youmatter.items.*;
import org.realverse.youmatter.replicator.ReplicatorBlock;
import org.realverse.youmatter.replicator.ReplicatorBlockEntity;
import org.realverse.youmatter.replicator.ReplicatorMenu;
import org.realverse.youmatter.scanner.ScannerBlock;
import org.realverse.youmatter.scanner.ScannerBlockEntity;
import org.realverse.youmatter.scanner.ScannerMenu;
import org.realverse.youmatter.util.loot.AddItemModifier;

import java.util.function.Supplier;


public class ModContent {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, YouMatter.MODID);
    public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ADD_ITEM = LOOT_MODIFIERS.register("add_item", () -> AddItemModifier.CODEC);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, YouMatter.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, YouMatter.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, YouMatter.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, YouMatter.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, YouMatter.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, YouMatter.MODID);

    public static final DeferredHolder<Block, ScannerBlock> SCANNER_BLOCK = BLOCKS.register("scanner", () -> new ScannerBlock());
    public static final DeferredHolder<MenuType<?>, MenuType<ScannerMenu>> SCANNER_MENU = MENU_TYPES.register("scanner", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ScannerMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerBlockEntity>> SCANNER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("scanner", () -> BlockEntityType.Builder.of(ScannerBlockEntity::new, SCANNER_BLOCK.get()).build(null));
    public static final DeferredHolder<Item, BlockItem> SCANNER_BLOCK_ITEM = ITEMS.register("scanner", () -> new BlockItem(SCANNER_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Block, EncoderBlock> ENCODER_BLOCK = BLOCKS.register("encoder", () -> new EncoderBlock());
    public static final DeferredHolder<MenuType<?>, MenuType<EncoderMenu>> ENCODER_MENU = MENU_TYPES.register("encoder", () -> IMenuTypeExtension.create((windowId, inv, data) -> new EncoderMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EncoderBlockEntity>> ENCODER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("encoder", () -> BlockEntityType.Builder.of(EncoderBlockEntity::new, ENCODER_BLOCK.get()).build(null));
    public static final DeferredHolder<Item, BlockItem> ENCODER_BLOCK_ITEM = ITEMS.register("encoder", () -> new BlockItem(ENCODER_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Block, CreatorBlock> CREATOR_BLOCK = BLOCKS.register("creator", () -> new CreatorBlock());
    public static final DeferredHolder<MenuType<?>, MenuType<CreatorMenu>> CREATOR_MENU = MENU_TYPES.register("creator", () -> IMenuTypeExtension.create((windowId, inv, data) -> new CreatorMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreatorBlockEntity>> CREATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("creator", () -> BlockEntityType.Builder.of(CreatorBlockEntity::new, CREATOR_BLOCK.get()).build(null));
    public static final DeferredHolder<Item, BlockItem> CREATOR_BLOCK_ITEM = ITEMS.register("creator", () -> new BlockItem(CREATOR_BLOCK.get(), new Item.Properties()));

    /**
     * @deprecated in favor of *new* "U-Matter Creator" block.
     * Since 3.0.0-beta.3
     */
    public static final DeferredHolder<Block, CreatorBlockOld> CREATOR_BLOCK_OLD = BLOCKS.register("creator_old", () -> new CreatorBlockOld());
    public static final DeferredHolder<MenuType<?>, MenuType<CreatorMenuOld>> CREATOR_MENU_OLD = MENU_TYPES.register("creator_old", () -> IMenuTypeExtension.create((windowId, inv, data) -> new CreatorMenuOld(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreatorBlockEntityOld>> CREATOR_BLOCK_ENTITY_OLD = BLOCK_ENTITY_TYPES.register("creator_old", () -> BlockEntityType.Builder.of(CreatorBlockEntityOld::new, CREATOR_BLOCK_OLD.get()).build(null));
    public static final DeferredHolder<Item, BlockItem> CREATOR_BLOCK_ITEM_OLD = ITEMS.register("creator_old", () -> new BlockItem(CREATOR_BLOCK_OLD.get(), new Item.Properties()));

    public static final DeferredHolder<Block, ReplicatorBlock> REPLICATOR_BLOCK = BLOCKS.register("replicator", () -> new ReplicatorBlock());
    public static final DeferredHolder<MenuType<?>, MenuType<ReplicatorMenu>> REPLICATOR_MENU = MENU_TYPES.register("replicator", () -> IMenuTypeExtension.create((windowId, inv, data) -> new ReplicatorMenu(windowId, inv.player.level(), data.readBlockPos(), inv, inv.player)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReplicatorBlockEntity>> REPLICATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("replicator", () -> BlockEntityType.Builder.of(ReplicatorBlockEntity::new, REPLICATOR_BLOCK.get()).build(null));
    public static final DeferredHolder<Item, BlockItem> REPLICATOR_BLOCK_ITEM = ITEMS.register("replicator", () -> new BlockItem(REPLICATOR_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<FluidType, FluidType> STABILIZER_TYPE = FLUID_TYPES.register("stabilizer", () -> new StabilizerFluidType());
    public static final DeferredHolder<Fluid, FlowingFluid> STABILIZER = FLUIDS.register("stabilizer", () -> new BaseFlowingFluid.Source(ModContent.STABILIZER_PROPERIES));
    public static final DeferredHolder<Fluid, FlowingFluid> STABILIZER_FLOWING = FLUIDS.register("stabilizer_flowing", () -> new BaseFlowingFluid.Flowing(ModContent.STABILIZER_PROPERIES));
    public static final DeferredHolder<Block, StabilizerFluidBlock> STABILIZER_FLUID_BLOCK = BLOCKS.register("stabilizer_fluid_block", () -> new StabilizerFluidBlock(STABILIZER.get(), BlockBehaviour.Properties.of().noCollission().strength(1.0F).noLootTable()));
    public static final DeferredHolder<Item, BucketItem> STABILIZER_BUCKET = ITEMS.register("stabilizer_bucket", () -> new BucketItem(STABILIZER.get(), (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties STABILIZER_PROPERIES = new BaseFlowingFluid.Properties(STABILIZER_TYPE, STABILIZER, STABILIZER_FLOWING).bucket(STABILIZER_BUCKET).block(STABILIZER_FLUID_BLOCK);

    public static final DeferredHolder<FluidType, FluidType> UMATTER_TYPE = FLUID_TYPES.register("umatter", () -> new UMatterFluidType());
    public static final DeferredHolder<Fluid, FlowingFluid> UMATTER = FLUIDS.register("umatter", () -> new BaseFlowingFluid.Source(ModContent.UMATTER_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> UMATTER_FLOWING = FLUIDS.register("umatter_flowing", () -> new BaseFlowingFluid.Flowing(ModContent.UMATTER_PROPERTIES));
    public static final DeferredHolder<Block, UMatterFluidBlock> UMATTER_FLUID_BLOCK = BLOCKS.register("umatter_fluid_block", () -> new UMatterFluidBlock(UMATTER.get(), BlockBehaviour.Properties.of().noCollission().strength(1.0F).noLootTable()));
    public static final DeferredHolder<Item, BucketItem> UMATTER_BUCKET = ITEMS.register("umatter_bucket", () -> new BucketItem(UMATTER.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final BaseFlowingFluid.Properties UMATTER_PROPERTIES = new BaseFlowingFluid.Properties(UMATTER_TYPE, UMATTER, UMATTER_FLOWING).bucket(UMATTER_BUCKET).block(UMATTER_FLUID_BLOCK);

    public static final DeferredHolder<Item, SteelIngotItem> STEEL_INGOT_ITEM = ITEMS.register("steel_ingot", () -> new SteelIngotItem());
    public static final DeferredHolder<Item, BlackHoleItem> BLACK_HOLE_ITEM = ITEMS.register("black_hole", () -> new BlackHoleItem());
    public static final DeferredHolder<Item, ThumbdriveItem> THUMBDRIVE_ITEM = ITEMS.register("thumb_drive", () -> new ThumbdriveItem());
    public static final DeferredHolder<Item, SteelCasingItem> STEEL_CASING_ITEM = ITEMS.register("steel_casing", () -> new SteelCasingItem());
    public static final DeferredHolder<Item, ComputeModuleItem> COMPUTE_MODULE_ITEM = ITEMS.register("compute_module", () -> new ComputeModuleItem());
    public static final DeferredHolder<Item, TransistorItem> TRANSISTOR_ITEM = ITEMS.register("transistor", () -> new TransistorItem());
    public static final DeferredHolder<Item, TransistorRawItem> TRANSISTOR_RAW_ITEM = ITEMS.register("transistor_raw", () -> new TransistorRawItem());

    public static void init(IEventBus modEventBus) {
        LOOT_MODIFIERS.register(modEventBus);
        BLOCKS.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
    }
}
