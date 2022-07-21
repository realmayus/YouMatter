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

    @ObjectHolder(YouMatter.MODID + ":black_hole")
    public static BlackHoleItem BLACK_HOLE_ITEM;

    @ObjectHolder(YouMatter.MODID + ":thumb_drive")
    public static ThumbdriveItem THUMBDRIVE_ITEM;

    @ObjectHolder(YouMatter.MODID + ":scanner")
    public static ScannerBlock SCANNER_BLOCK;
    @ObjectHolder(YouMatter.MODID + ":scanner")
    public static MenuType<ScannerMenu> SCANNER_MENU_TYPE;
    @ObjectHolder(YouMatter.MODID + ":scanner")
    public static BlockEntityType<ScannerBlockEntity> SCANNER_BLOCK_ENTITY_TYPE;

    @ObjectHolder(YouMatter.MODID + ":encoder")
    public static EncoderBlock ENCODER_BLOCK;
    @ObjectHolder(YouMatter.MODID + ":encoder")
    public static MenuType<EncoderMenu> ENCODER_MENU_TYPE;
    @ObjectHolder(YouMatter.MODID + ":encoder")
    public static BlockEntityType<EncoderBlockEntity> ENCODER_BLOCK_ENTITY_TYPE;

    @ObjectHolder(YouMatter.MODID + ":creator")
    public static CreatorBlock CREATOR_BLOCK;
    @ObjectHolder(YouMatter.MODID + ":creator")
    public static MenuType<CreatorMenu> CREATOR_MENU_TYPE;
    @ObjectHolder(YouMatter.MODID + ":creator")
    public static BlockEntityType<CreatorBlockEntity> CREATOR_BLOCK_ENTITY_TYPE;

    @ObjectHolder(YouMatter.MODID + ":replicator")
    public static ReplicatorBlock REPLICATOR_BLOCK;
    @ObjectHolder(YouMatter.MODID + ":replicator")
    public static MenuType<ReplicatorMenu> REPLICATOR_MENU_TYPE;
    @ObjectHolder(YouMatter.MODID + ":replicator")
    public static BlockEntityType<ReplicatorBlockEntity> REPLICATOR_BLOCK_ENTITY_TYPE;

    @ObjectHolder(YouMatter.MODID + ":stabilizer_bucket")
    public static BucketItem STABILIZER_BUCKET;
    @ObjectHolder(YouMatter.MODID + ":umatter_bucket")
    public static BucketItem UMATTER_BUCKET;
}
