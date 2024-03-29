package realmayus.youmatter.replicator;

import java.util.stream.IntStream;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import realmayus.youmatter.ModContent;

public class ReplicatorBlock extends BaseEntityBlock {

    public ReplicatorBlock() {
        super(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReplicatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModContent.REPLICATOR_BLOCK_ENTITY.get(), ReplicatorBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof ReplicatorBlockEntity replicator) {
                replicator.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i))));
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    //    int currentDepth = 0;
    //    int result = 0;
    //    public int recurse(ItemStack is, RecipeManager recipeManager) {
    //        System.out.println("New Recursion, depth is: " + currentDepth);
    //        List<IRecipe<?>> matchingRecipes = GeneralUtils.getMatchingRecipes(recipeManager, is);
    //        for (IRecipe<?> recipe : matchingRecipes) {
    //            if (currentDepth < 747598378) {
    //                for(Ingredient ingredient : recipe.getIngredients()) {
    //                    if (!GeneralUtils.hasCustomUMatterValue(ingredient.getMatchingStacks())) {
    //                        //recursion
    //                        int cheapestVariant = Integer.MAX_VALUE;
    //                        for (ItemStack variant : ingredient.getMatchingStacks()) {
    //                            System.out.println("Variant: " + variant.toString());
    //                            if (!GeneralUtils.getMatchingRecipes(recipeManager, variant).isEmpty()) { //Item has Override!
    //                                int currentAmount = recurse(variant, recipeManager);
    //                                if (currentAmount < cheapestVariant) {
    //                                    cheapestVariant = currentAmount;
    //                                }
    //                            }
    //                        }
    //                        if (cheapestVariant == Integer.MAX_VALUE) {
    //                            System.out.println("Falling back to default value for current variant");
    //                            result = result + YMConfig.CONFIG.defaultAmount.get();
    //                        } else {
    //                            System.out.println("Cheapest variant found costs " + cheapestVariant + "mB");
    //                        }
    //                    } else {
    //                        result = result + YMConfig.CONFIG.defaultAmount.get();
    //                    }
    //                }
    //            } else {
    //                result = result + YMConfig.CONFIG.defaultAmount.get();
    //            }
    //        }
    //        currentDepth++;
    //        return result;
    //    }

    /**
     * EVENT that is called when you right-click the block,
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            MenuProvider menuProvider = getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHooks.openScreen(serverPlayer, menuProvider, pos);
                }
            }

        }
        return InteractionResult.SUCCESS;
    }
    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ReplicatorBlockEntity replicator ? replicator : null;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof ReplicatorBlockEntity){
            be.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i)))));
        }
        super.playerWillDestroy(level, pos, state, player);
    }

}
