package realmayus.youmatter.creator;

import java.util.stream.IntStream;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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

public class CreatorBlock extends BaseEntityBlock {


    public CreatorBlock() {
        super(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModContent.CREATOR_BLOCK_ENTITY.get(), CreatorBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CreatorBlockEntity creator) {
                creator.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i))));
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    /**
     * EVENT that is called when you right-click the block,
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
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
        return level.getBlockEntity(pos) instanceof CreatorBlockEntity creator ? creator : null;
    }

}
