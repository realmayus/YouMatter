package realmayus.youmatter.fluid;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import realmayus.youmatter.YouMatter;

public class StabilizerFluidType extends FluidType {
    public StabilizerFluidType() {
        super(FluidType.Properties.create()
                .descriptionId("block.youmatter.stabilizer")
                .fallDistanceModifier(0.0F)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(false)
                .canHydrate(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions(){
            private static final ResourceLocation STILL_TEXTURE = new ResourceLocation(YouMatter.MODID, "block/stabilizer_still");
            private static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(YouMatter.MODID, "block/stabilizer_flow");

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
