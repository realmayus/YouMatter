package realmayus.youmatter;



import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import realmayus.youmatter.creator.BlockCreator;
import realmayus.youmatter.encoder.BlockEncoder;
import realmayus.youmatter.items.*;
import realmayus.youmatter.replicator.BlockReplicator;
import realmayus.youmatter.scanner.ScannerBlock;
import realmayus.youmatter.scanner.ScannerContainer;
import realmayus.youmatter.scanner.ScannerTile;

@ObjectHolder(YouMatter.MODID)
public class ObjectHolders {
//    public static final BlockFluidClassic UMATTER_BLOCK = new BlockFluidClassic(ModFluids.UMATTER, ModMaterials.UMATTER);
//    public static final BlockFluidClassic STABILIZER_BLOCK = new BlockFluidClassic(ModFluids.STABILIZER, ModMaterials.STABILIZER);


    public static final BlockReplicator REPLICATOR = null;
    public static final BlockCreator CREATOR = null;
    public static final ScannerBlock SCANNER = null;
    public static final BlockEncoder ENCODER = null;

    public static final ThumbdriveItem THUMB_DRIVE = null;
//    public static final ItemBlock UMATTER_BLOCK = new ItemBlock(ObjectHolders.UMATTER_BLOCK);
//    public static final ItemBlock STABILIZER_BLOCK = new ItemBlock(ObjectHolders.STABILIZER_BLOCK);
    public static final MachineCasingItem MACHINE_CASING = null;
    public static final TransistorRawItem TRANSISTOR_RAW = null;
    public static final TransistorItem TRANSISTOR = null;
    public static final ComputeModuleItem COMPUTE_MODULE = null;
    public static final BlackHoleItem BLACK_HOLE_ITEM = null;

    public static final ContainerType<ScannerContainer> SCANNER_CONTAINER_TYPE = null;
    public static final TileEntityType<ScannerTile> SCANNER_TILE_ENTITY_TYPE = null;
}
