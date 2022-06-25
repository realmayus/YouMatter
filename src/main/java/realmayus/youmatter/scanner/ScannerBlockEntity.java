package realmayus.youmatter.scanner;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderBlockEntity;
import realmayus.youmatter.util.MyEnergyStorage;
import realmayus.youmatter.util.RegistryUtil;

public class ScannerBlockEntity extends BlockEntity implements MenuProvider {

    public boolean hasEncoder = false;

    public ScannerBlockEntity(BlockPos pos, BlockState state) {
        super(ObjectHolders.SCANNER_BLOCK_ENTITY, pos, state);
    }

    public boolean getHasEncoder() {
        return hasEncoder;
    }

    public void setHasEncoder(boolean hasEncoder) {
        this.hasEncoder = hasEncoder;
        setChanged();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventory.cast();
        }
        if(cap == CapabilityEnergy.ENERGY) {
            return myEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }
    public LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            ScannerBlockEntity.this.setChanged();
        }
    });

    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setChanged();
    }

    public int getEnergy() {
        return myEnergyStorage.resolve().get().getEnergyStored();
    }

    public void setEnergy(int energy) {
        myEnergyStorage.resolve().get().setEnergy(energy);
    }

    private LazyOptional<MyEnergyStorage> myEnergyStorage = LazyOptional.of(() -> new MyEnergyStorage(this, 1000000, Integer.MAX_VALUE));

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("progress")) {
            setProgress(compound.getInt("progress"));
        }
        if (compound.contains("energy")) {
            setEnergy(compound.getInt("energy"));
        }
        if(compound.contains("inventory")) {
            inventory.resolve().get().deserializeNBT((CompoundTag) compound.get("inventory"));
        }

        setHasEncoder(compound.getBoolean("encoder"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        compound.putBoolean("encoder", getHasEncoder());
        if (inventory != null) {
            compound.put("inventory", inventory.resolve().get().serializeNBT());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private int currentPartTick = 0;
    public static void tick(Level level, BlockPos pos, BlockState state, ScannerBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(currentPartTick >= 2) {
            BlockPos encoderPos = getNeighborEncoder(this.worldPosition);

            if (encoderPos != null) {
                if(!hasEncoder) {
                    setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                }

                hasEncoder = true;
                inventory.ifPresent(inventory -> {
                    if(!inventory.getStackInSlot(1).isEmpty() && isItemAllowed(inventory.getStackInSlot(1))) {
                        if(getEnergy() > YMConfig.CONFIG.energyScanner.get()) {
                            if (getProgress() < 100) {
                                setProgress(getProgress() + 1);
                                myEnergyStorage.ifPresent(myEnergyStorage -> myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyScanner.get(), false));
                            } else {
                                // Notifying the neighboring encoder of this scanner having finished its operation
                                ((EncoderBlockEntity)level.getBlockEntity(encoderPos)).ignite(inventory.getStackInSlot(1)); //don't worry, this is already checked by getNeighborEncoder() c:
                                inventory.setStackInSlot(1, ItemStack.EMPTY);
                                setProgress(0);
                            }
                        }
                    } else if (getProgress() != 0) {
                        setProgress(0); // if item was suddenly removed, reset progress to 0
                    }
                });
            } else {
                if(hasEncoder) {
                    setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                }

                hasEncoder = false;
            }
            currentPartTick = 0;
        } else {
            currentPartTick++;
        }
    }

    private boolean isItemAllowed(ItemStack itemStack) {

        boolean matches = YMConfig.CONFIG.filterItems.get().stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(RegistryUtil.getRegistryName(itemStack.getItem())).toString()));
        //If list should act as a blacklist AND it contains the item, disallow scanning
        if (YMConfig.CONFIG.filterMode.get() && matches) {
            return false;
            //If list should act as a whitelist AND it DOESN'T contain the item, disallow scanning
        } else if (YMConfig.CONFIG.filterMode.get() || matches) return true;
        else return false;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventory.invalidate();
        myEnergyStorage.invalidate();
    }

    @Nullable
    private BlockPos getNeighborEncoder(BlockPos scannerPos) {
        for(Direction facing : Direction.values()) {
            BlockPos offsetPos = scannerPos.relative(facing);

            if(level.getBlockState(offsetPos).getBlock() instanceof EncoderBlock) {
                if(level.getBlockEntity(offsetPos) instanceof EncoderBlockEntity) {
                    return offsetPos;
                }
            }
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ObjectHolders.SCANNER_BLOCK.getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new ScannerMenu(windowID, level, worldPosition, playerInventory, player);
    }
}
