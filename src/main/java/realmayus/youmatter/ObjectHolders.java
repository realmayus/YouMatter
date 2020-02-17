package realmayus.youmatter;


import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BucketItem;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import realmayus.youmatter.creator.CreatorBlock;
import realmayus.youmatter.creator.CreatorContainer;
import realmayus.youmatter.creator.CreatorTile;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderContainer;
import realmayus.youmatter.encoder.EncoderTile;
import realmayus.youmatter.items.BlackHoleItem;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerContainer;
import realmayus.youmatter.scanner.ScannerTile;

public class ObjectHolders {
//    public static final BlockFluidClassic UMATTER_BLOCK = new BlockFluidClassic(ModFluids.UMATTER, ModMaterials.UMATTER);
//    public static final BlockFluidClassic STABILIZER_BLOCK = new BlockFluidClassic(ModFluids.STABILIZER, ModMaterials.STABILIZER);

//
//    public static final BlockReplicator REPLICATOR = null;
//    public static final BlockCreator CREATOR = null;

//    public static final BlockEncoder ENCODER = null;
//
//    public static final ThumbdriveItem THUMB_DRIVE = null;
//    public static final ItemBlock UMATTER_BLOCK = new ItemBlock(ObjectHolders.UMATTER_BLOCK);
//    public static final ItemBlock STABILIZER_BLOCK = new ItemBlock(ObjectHolders.STABILIZER_BLOCK);
//    public static final MachineCasingItem MACHINE_CASING = null;
//    public static final TransistorRawItem TRANSISTOR_RAW = null;
//    public static final TransistorItem TRANSISTOR = null;
//    public static final ComputeModuleItem COMPUTE_MODULE = null;

    @ObjectHolder(YouMatter.MODID + ":black_hole")
    public static BlackHoleItem BLACK_HOLE_ITEM;

    @ObjectHolder(YouMatter.MODID + ":thumb_drive")
    public static ThumbdriveItem THUMBDRIVE_ITEM;

    @ObjectHolder(YouMatter.MODID + ":scanner")
    public static ScannerBlock SCANNER_BLOCK;
    @ObjectHolder(YouMatter.MODID + ":scanner")
    public static ContainerType<ScannerContainer> SCANNER_CONTAINER;
    @ObjectHolder(YouMatter.MODID + ":scanner")
    public static TileEntityType<ScannerTile> SCANNER_TILE;

    @ObjectHolder(YouMatter.MODID + ":encoder")
    public static EncoderBlock ENCODER_BLOCK;
    @ObjectHolder(YouMatter.MODID + ":encoder")
    public static ContainerType<EncoderContainer> ENCODER_CONTAINER;
    @ObjectHolder(YouMatter.MODID + ":encoder")
    public static TileEntityType<EncoderTile> ENCODER_TILE;

    @ObjectHolder(YouMatter.MODID + ":creator")
    public static CreatorBlock CREATOR_BLOCK;
    @ObjectHolder(YouMatter.MODID + ":creator")
    public static ContainerType<CreatorContainer> CREATOR_CONTAINER;
    @ObjectHolder(YouMatter.MODID + ":creator")
    public static TileEntityType<CreatorTile> CREATOR_TILE;

//    @ObjectHolder(YouMatter.MODID + ":replicator")
//    public static ReplicatorBlock REPLICATOR_BLOCK;
//    @ObjectHolder(YouMatter.MODID + ":replicator")
//    public static ContainerType<ReplicatorContainer> REPLICATOR_CONTAINER;
//    @ObjectHolder(YouMatter.MODID + ":replicator")
//    public static TileEntityType<CreatorTile> CREATOR_TILE;

    @ObjectHolder(YouMatter.MODID + ":stabilizer_bucket")
    public static BucketItem STABILIZER_BUCKET;

    @ObjectHolder(YouMatter.MODID + ":umatter_bucket")
    public static BucketItem UMATTER_BUCKET;
}
