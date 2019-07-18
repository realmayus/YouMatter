package realmayus.youmatter;

import net.minecraft.util.ResourceLocation;

import realmayus.youmatter.umatter.ModFluid;

public class ModFluids {
    public static final ModFluid UMATTER = (ModFluid) new ModFluid(
            "umatter",
            new ResourceLocation(YouMatter.MODID,"umatter_still"),
            new ResourceLocation(YouMatter.MODID, "umatter_flow")
    )
            .setMaterial(ModMaterials.UMATTER)
            .setDensity(1100)
            .setGaseous(false)
            .setLuminosity(9)
            .setViscosity(1000)
            .setTemperature(300);
}
