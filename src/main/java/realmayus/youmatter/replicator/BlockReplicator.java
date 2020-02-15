package realmayus.youmatter.replicator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.creator.TileCreator;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class BlockReplicator extends Block {


    //Creation of a so called "BlockState" for saving the direction the block is placed in
    private static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);


    public BlockReplicator() {
        super(Material.IRON);

        setTranslationKey(YouMatter.MODID + ".replicator");
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getBlockState().getBaseState().withProperty(FACING_HORIZ, EnumFacing.NORTH));
        setHardness(5.0F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileReplicator();
    }


    /**
     * EVENT that is called when you right-click the block,
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);

        if(!player.isSneaking()) {
            player.openGui(YouMatter.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        } else {
            return false;
        }

        return true;
    }



    /**
     * Returning the BlockState for the direction so the Block actually shows the texture correctly.
     */
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite());
    }


    /**
     * A couple of necessary methods for creating and getting the BlockState, nothing fancy here.
     */
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumFacing = EnumFacing.byIndex(meta);
        if (enumFacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumFacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING_HORIZ, enumFacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING_HORIZ).getIndex();
    }

    @Override
    public void breakBlock( World worldIn, BlockPos pos, IBlockState state ){
        TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof TileReplicator){
            IItemHandler itemStackHandler = te.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (itemStackHandler != null) {
                IntStream.range(0, itemStackHandler.getSlots()).forEach(i -> worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStackHandler.getStackInSlot(i))));
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

}
