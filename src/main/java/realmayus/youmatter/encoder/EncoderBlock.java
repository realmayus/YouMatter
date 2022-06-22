package realmayus.youmatter.encoder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.IWorld;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import realmayus.youmatter.scanner.ScannerTile;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class EncoderBlock extends Block {

    public EncoderBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F).sound(SoundType.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new EncoderTile();
    }

    /**
     * EVENT that is called when you right-click the block,
     */
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide) {
            MenuProvider containerProvider = getMenuProvider(state, worldIn, pos);
            if (containerProvider != null) {
                if (player instanceof ServerPlayer) {
                    NetworkHooks.openGui((ServerPlayer) player, containerProvider, pos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        return te instanceof EncoderTile ? (MenuProvider)te : null;
    }
    
    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if(te instanceof EncoderTile){
            te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i)))));
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }
}
