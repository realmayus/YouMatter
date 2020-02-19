package realmayus.youmatter.scanner;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class ScannerBlock extends Block {

    public ScannerBlock() {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F).sound(SoundType.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    /**
     * EVENT that is called when you right-click the block,
     */
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
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

        return te instanceof ScannerTile ? (INamedContainerProvider)te : null;
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ScannerTile();
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null) {
            if(te instanceof ScannerTile){
                te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> worldIn.addEntity(new ItemEntity(worldIn.getWorld(), pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i)))));

            }
        }
        super.onPlayerDestroy(worldIn, pos, state);
    }
}
