package realmayus.youmatter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import realmayus.youmatter.fluid.StabilizerFluidBlock;

import net.minecraft.block.AbstractBlock;

public class ModFluids {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, YouMatter.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, YouMatter.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, YouMatter.MODID);

    public static RegistryObject<FlowingFluid> STABILIZER = FLUIDS.register("stabilizer", () -> new ForgeFlowingFluid.Source(ModFluids.STABILIZER_PROPERIES));
    public static RegistryObject<FlowingFluid> STABILIZER_FLOWING = FLUIDS.register("stabilizer_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.STABILIZER_PROPERIES));
    public static RegistryObject<StabilizerFluidBlock> STABILIZER_FLUID_BLOCK = BLOCKS.register("stabilizer_fluid_block", () -> new StabilizerFluidBlock(STABILIZER, AbstractBlock.Properties.of(Material.LAVA).noCollission().strength(1.0F).noDrops()));

    public static RegistryObject<Item> STABILIZER_BUCKET = ITEMS.register("stabilizer_bucket", () -> new BucketItem(STABILIZER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(YouMatter.ITEM_GROUP)));
    public static final ForgeFlowingFluid.Properties STABILIZER_PROPERIES = new ForgeFlowingFluid.Properties(STABILIZER, STABILIZER_FLOWING, FluidAttributes.builder(new ResourceLocation(YouMatter.MODID, "block/stabilizer_still"), new ResourceLocation(YouMatter.MODID, "block/stabilizer_flow"))).bucket(STABILIZER_BUCKET).block(STABILIZER_FLUID_BLOCK);

    public static RegistryObject<FlowingFluid> UMATTER = FLUIDS.register("umatter", () -> new ForgeFlowingFluid.Source(ModFluids.UMATTER_PROPERTIES));
    public static RegistryObject<FlowingFluid> UMATTER_FLOWING = FLUIDS.register("umatter_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.UMATTER_PROPERTIES));
    public static RegistryObject<StabilizerFluidBlock> UMATTER_FLUID_BLOCK = BLOCKS.register("umatter_fluid_block", () -> new StabilizerFluidBlock(UMATTER, AbstractBlock.Properties.of(Material.LAVA).noCollission().strength(1.0F).noDrops()));

    public static RegistryObject<Item> UMATTER_BUCKET = ITEMS.register("umatter_bucket", () -> new BucketItem(UMATTER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(YouMatter.ITEM_GROUP)));
    public static final ForgeFlowingFluid.Properties UMATTER_PROPERTIES = new ForgeFlowingFluid.Properties(UMATTER, UMATTER_FLOWING, FluidAttributes.builder(new ResourceLocation(YouMatter.MODID, "block/umatter_still"), new ResourceLocation(YouMatter.MODID, "block/umatter_flow"))).bucket(UMATTER_BUCKET).block(UMATTER_FLUID_BLOCK);

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}
