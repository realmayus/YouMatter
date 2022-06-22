package realmayus.youmatter.replicator;

import java.util.stream.IntStream;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import realmayus.youmatter.ObjectHolders;

public class ReplicatorBlock extends BaseEntityBlock {

    public ReplicatorBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReplicatorTile(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ObjectHolders.REPLICATOR_TILE, ReplicatorTile::serverTick);
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {        if (!worldIn.isClientSide) {
        MenuProvider containerProvider = getMenuProvider(state, worldIn, pos);
        if (containerProvider != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openGui(serverPlayer, containerProvider, pos);
            }
        }

    }
    return InteractionResult.SUCCESS;
    }
    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        return te instanceof ReplicatorTile replicator ? replicator : null;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if(te instanceof ReplicatorTile){
            te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i)))));
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

}
