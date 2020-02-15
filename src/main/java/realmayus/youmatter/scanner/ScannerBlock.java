package realmayus.youmatter.scanner;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;

public class ScannerBlock extends Block {

    public ScannerBlock() {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F).sound(SoundType.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ScannerTile();
    }

    /**
     * EVENT that is called when you right-click the block,
     */
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
            if (containerProvider != null) {
                player.openContainer(containerProvider);
            }
        }
        return true;
    }

//
//    @Override
//    public void breakBlock( World worldIn, BlockPos pos, IBlockState state ){
//        TileEntity te = worldIn.getTileEntity(pos);
//        if(te instanceof TileScanner){
//            IItemHandler itemStackHandler = te.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
//            if (itemStackHandler != null) {
//                IntStream.range(0, itemStackHandler.getSlots()).forEach(i -> worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStackHandler.getStackInSlot(i))));
//            }
//        }
//        super.breakBlock(worldIn, pos, state);
//    } //TODO drop items
}
