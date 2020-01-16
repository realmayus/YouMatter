package realmayus.youmatter;


import io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.registry.GameRegistry;
import realmayus.youmatter.creator.BlockCreator;
import realmayus.youmatter.encoder.BlockEncoder;
import realmayus.youmatter.replicator.BlockReplicator;
import realmayus.youmatter.scanner.BlockScanner;

@GameRegistry.ObjectHolder(YouMatter.MODID)
public class ModBlocks {
    public static final BlockFluidClassic UMATTER_BLOCK = new BlockFluidClassic(ModFluids.UMATTER, ModMaterials.UMATTER);
    public static final BlockFluidClassic STABILIZER_BLOCK = new BlockFluidClassic(ModFluids.STABILIZER, ModMaterials.STABILIZER);


    public static final BlockReplicator REPLICATOR = null;
    public static final BlockCreator CREATOR = null;
    public static final BlockScanner SCANNER = null;
    public static final BlockEncoder ENCODER = null;
}
