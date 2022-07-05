package realmayus.youmatter.fluid;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import realmayus.youmatter.YouMatter;

public class UMatterFluidType extends FluidType {
    public UMatterFluidType() {
        super(FluidType.Properties.create()
                .descriptionId("block.youmatter.umatter")
                .fallDistanceModifier(0.0F)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(false)
                .canHydrate(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
    }

    @Override
    public void initializeClient(Consumer<IFluidTypeRenderProperties> consumer) {
        consumer.accept(new IFluidTypeRenderProperties(){
            private static final ResourceLocation STILL_TEXTURE = new ResourceLocation(YouMatter.MODID, "block/umatter_still");
            private static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(YouMatter.MODID, "block/umatter_flow");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }
        });
    }
}
