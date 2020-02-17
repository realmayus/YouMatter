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

public class ModFluids {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, YouMatter.MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, YouMatter.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, YouMatter.MODID);

    public static RegistryObject<FlowingFluid> stabilizer = FLUIDS.register("stabilizer", () -> new ForgeFlowingFluid.Source(ModFluids.STABILIZER_PROPERIES));
    public static RegistryObject<FlowingFluid> stabilizer_flowing = FLUIDS.register("stabilizer_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.STABILIZER_PROPERIES));
    public static RegistryObject<StabilizerFluidBlock> stabilizer_fluid_block = BLOCKS.register("stabilizer_fluid_block", () -> new StabilizerFluidBlock(stabilizer, Block.Properties.create(Material.LAVA).doesNotBlockMovement().hardnessAndResistance(1.0F).noDrops()));

    public static RegistryObject<Item> STABILIZER_BUCKET = ITEMS.register("stabilizer_bucket", () -> new BucketItem(stabilizer, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(YouMatter.ITEM_GROUP)));
    public static final ForgeFlowingFluid.Properties STABILIZER_PROPERIES = new ForgeFlowingFluid.Properties(stabilizer, stabilizer_flowing, FluidAttributes.builder(new ResourceLocation(YouMatter.MODID, "block/stabilizer_still"), new ResourceLocation(YouMatter.MODID, "block/stabilizer_flow"))).bucket(STABILIZER_BUCKET).block(stabilizer_fluid_block);

    public static RegistryObject<FlowingFluid> umatter = FLUIDS.register("umatter", () -> new ForgeFlowingFluid.Source(ModFluids.umatter_properties));
    public static RegistryObject<FlowingFluid> umatter_flowing = FLUIDS.register("umatter_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.umatter_properties));
    public static RegistryObject<StabilizerFluidBlock> umatter_fluid_block = BLOCKS.register("umatter_fluid_block", () -> new StabilizerFluidBlock(umatter, Block.Properties.create(Material.LAVA).doesNotBlockMovement().hardnessAndResistance(1.0F).noDrops()));

    public static RegistryObject<Item> umatter_bucket = ITEMS.register("umatter_bucket", () -> new BucketItem(umatter, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(YouMatter.ITEM_GROUP)));
    public static final ForgeFlowingFluid.Properties umatter_properties = new ForgeFlowingFluid.Properties(umatter, umatter_flowing, FluidAttributes.builder(new ResourceLocation(YouMatter.MODID, "block/umatter_still"), new ResourceLocation(YouMatter.MODID, "block/umatter_flow"))).bucket(umatter_bucket).block(umatter_fluid_block);

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}
