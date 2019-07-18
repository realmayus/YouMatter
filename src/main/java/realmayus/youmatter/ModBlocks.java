package realmayus.youmatter;


import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.registry.GameRegistry;
import realmayus.youmatter.replicator.BlockReplicator;

@GameRegistry.ObjectHolder(YouMatter.MODID)
public class ModBlocks {
    public static final BlockFluidClassic UMATTER_BLOCK = new BlockFluidClassic(ModFluids.UMATTER, ModMaterials.UMATTER);


    public static final BlockReplicator REPLICATOR = null;
}
