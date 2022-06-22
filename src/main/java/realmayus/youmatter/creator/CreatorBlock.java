package realmayus.youmatter.creator;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class CreatorBlock extends Block {


    public CreatorBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F).sound(SoundType.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new CreatorTile();
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

        return te instanceof CreatorTile ? (MenuProvider)te : null;
    }

}
