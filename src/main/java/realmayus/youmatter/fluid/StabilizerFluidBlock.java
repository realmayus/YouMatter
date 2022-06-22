package realmayus.youmatter.fluid;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class StabilizerFluidBlock extends LiquidBlock {
    public StabilizerFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 5));
        }
        super.entityInside(state, worldIn, pos, entityIn);
    }


}
