package org.realverse.youmatter.fluid;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class UMatterFluidType extends FluidType {
    public UMatterFluidType() {
        super(Properties.create()
                .descriptionId("block.youmatter.umatter")
                .fallDistanceModifier(0.0F)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(false)
                .canHydrate(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
    }
}
