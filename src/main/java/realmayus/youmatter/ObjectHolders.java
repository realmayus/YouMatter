package realmayus.youmatter;


import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;
import realmayus.youmatter.creator.CreatorBlock;
import realmayus.youmatter.creator.CreatorBlockEntity;
import realmayus.youmatter.creator.CreatorMenu;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderBlockEntity;
import realmayus.youmatter.encoder.EncoderMenu;
import realmayus.youmatter.items.BlackHoleItem;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.replicator.ReplicatorBlock;
import realmayus.youmatter.replicator.ReplicatorBlockEntity;
import realmayus.youmatter.replicator.ReplicatorMenu;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerBlockEntity;
import realmayus.youmatter.scanner.ScannerMenu;

public class ObjectHolders {

    //    public static final ThumbdriveItem THUMB_DRIVE = null;
    //    public static final ItemBlock UMATTER_BLOCK = new ItemBlock(ObjectHolders.UMATTER_BLOCK);
    //    public static final ItemBlock STABILIZER_BLOCK = new ItemBlock(ObjectHolders.STABILIZER_BLOCK);
    //    public static final MachineCasingItem MACHINE_CASING = null;
    //    public static final TransistorRawItem TRANSISTOR_RAW = null;
    //    public static final TransistorItem TRANSISTOR = null;
    //    public static final ComputeModuleItem COMPUTE_MODULE = null;

    @ObjectHolder(value = "minecraft:block", registryName = YouMatter.MODID + ":black_hole")
    public static BlackHoleItem BLACK_HOLE_ITEM;

    @ObjectHolder(value = "minecraft:item", registryName = YouMatter.MODID + ":thumb_drive")
    public static ThumbdriveItem THUMBDRIVE_ITEM;

    @ObjectHolder(value = "minecraft:block", registryName = YouMatter.MODID + ":scanner")
    public static ScannerBlock SCANNER_BLOCK;
    @ObjectHolder(value = "minecraft:menu_type", registryName = YouMatter.MODID + ":scanner")
    public static MenuType<ScannerMenu> SCANNER_CONTAINER;
    @ObjectHolder(value = "minecraft:block_entity", registryName = YouMatter.MODID + ":scanner")
    public static BlockEntityType<ScannerBlockEntity> SCANNER_TILE;

    @ObjectHolder(value = "minecraft:block", registryName = YouMatter.MODID + ":encoder")
    public static EncoderBlock ENCODER_BLOCK;
    @ObjectHolder(value = "minecraft:menu_type", registryName = YouMatter.MODID + ":encoder")
    public static MenuType<EncoderMenu> ENCODER_CONTAINER;
    @ObjectHolder(value = "minecraft:block_entity", registryName = YouMatter.MODID + ":encoder")
    public static BlockEntityType<EncoderBlockEntity> ENCODER_TILE;

    @ObjectHolder(value = "minecraft:block", registryName = YouMatter.MODID + ":creator")
    public static CreatorBlock CREATOR_BLOCK;
    @ObjectHolder(value = "minecraft:menu_type", registryName = YouMatter.MODID + ":creator")
    public static MenuType<CreatorMenu> CREATOR_CONTAINER;
    @ObjectHolder(value = "minecraft:block_entity", registryName = YouMatter.MODID + ":creator")
    public static BlockEntityType<CreatorBlockEntity> CREATOR_TILE;

    @ObjectHolder(value = "minecraft:block", registryName = YouMatter.MODID + ":replicator")
    public static ReplicatorBlock REPLICATOR_BLOCK;
    @ObjectHolder(value = "minecraft:menu_type", registryName = YouMatter.MODID + ":replicator")
    public static MenuType<ReplicatorMenu> REPLICATOR_CONTAINER;
    @ObjectHolder(value = "minecraft:block_entity", registryName = YouMatter.MODID + ":replicator")
    public static BlockEntityType<ReplicatorBlockEntity> REPLICATOR_TILE;

    @ObjectHolder(value = "minecraft:item", registryName = YouMatter.MODID + ":stabilizer_bucket")
    public static BucketItem STABILIZER_BUCKET;
    @ObjectHolder(value = "minecraft:item", registryName = YouMatter.MODID + ":umatter_bucket")
    public static BucketItem UMATTER_BUCKET;
}
