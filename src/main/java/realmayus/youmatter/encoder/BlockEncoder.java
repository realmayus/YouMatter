package realmayus.youmatter.encoder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;

public class BlockEncoder extends Block {

    private static final PropertyDirection FACING_HORIZ = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);


    public BlockEncoder() {
        super(Material.IRON);

        setTranslationKey(YouMatter.MODID + ".encoder");
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
        return new TileEncoder();
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
        EnumFacing enumfacing = EnumFacing.byIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING_HORIZ, enumfacing);    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING_HORIZ).getIndex();
    }
}
