package realmayus.youmatter.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Properties;

public class UMatterFluidBlock extends FlowingFluidBlock {
    public UMatterFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            ((LivingEntity)entityIn).addEffect(new EffectInstance(Effects.JUMP, 200, 5));
        }
        super.entityInside(state, worldIn, pos, entityIn);
    }
}
