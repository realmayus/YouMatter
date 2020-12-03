package realmayus.youmatter.replicator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class ReplicatorBlock extends Block {

    public ReplicatorBlock() {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F).sound(SoundType.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReplicatorTile();
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {        if (!worldIn.isRemote) {
            INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
            if (containerProvider != null) {
                if (player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, pos);
                }
            }

        }
        return ActionResultType.SUCCESS;
    }
    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);

        return te instanceof ReplicatorTile ? (INamedContainerProvider)te : null;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof ReplicatorTile){
            te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i)))));
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

}
