package org.realverse.youmatter.scanner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.realverse.youmatter.ModContent;
import org.realverse.youmatter.YMConfig;
import org.realverse.youmatter.encoder.EncoderBlock;
import org.realverse.youmatter.encoder.EncoderBlockEntity;
import org.realverse.youmatter.util.MyEnergyStorage;
import org.realverse.youmatter.util.RegistryUtil;

import javax.annotation.Nullable;
import java.util.Objects;

public class ScannerBlockEntity extends BlockEntity implements MenuProvider {

    public boolean hasEncoder = false;

    public ItemStackHandler inventory;
    private final MyEnergyStorage myEnergyStorage;

    public ScannerBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.SCANNER_BLOCK_ENTITY.get(), pos, state);
        this.inventory = new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                ScannerBlockEntity.this.setChanged();
            }
        };
        this.myEnergyStorage = new MyEnergyStorage(this, 1000000, Integer.MAX_VALUE);
    }

    public boolean getHasEncoder() {
        return hasEncoder;
    }

    public void setHasEncoder(boolean hasEncoder) {
        this.hasEncoder = hasEncoder;
        setChanged();
    }

    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setChanged();
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    public void setEnergy(int energy) {
        myEnergyStorage.setEnergy(energy);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (compound.contains("progress")) {
            setProgress(compound.getInt("progress"));
        }
        if (compound.contains("energy")) {
            setEnergy(compound.getInt("energy"));
        }
        if(compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }

        setHasEncoder(compound.getBoolean("encoder"));
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        compound.putBoolean("encoder", getHasEncoder());
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT(provider));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
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
                if(inventory != null) {
                    if(!inventory.getStackInSlot(1).isEmpty() && isItemAllowed(inventory.getStackInSlot(1))) {
                        if(getEnergy() > YMConfig.get().energyScanner) {
                            if (getProgress() < 100) {
                                setProgress(getProgress() + 1);
                                myEnergyStorage.extractEnergy(YMConfig.get().energyScanner, false);
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
                }
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

        boolean matches = YMConfig.get().filterItems.stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(RegistryUtil.getRegistryName(itemStack.getItem())).toString()));
        //If list should act as a blacklist AND it contains the item, disallow scanning
        if (YMConfig.get().filterMode && matches) {
            return false;
            //If list should act as a whitelist AND it DOESN'T contain the item, disallow scanning
        } else if (YMConfig.get().filterMode || matches) return true;
        else return false;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCapabilities();
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
        return Component.translatable(ModContent.SCANNER_BLOCK.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new ScannerMenu(windowID, level, worldPosition, playerInventory, player);
    }

    public ItemStackHandler getItemHandler() {
        return this.inventory;
    }

    public IEnergyStorage getEnergyHandler() {
        return this.myEnergyStorage;
    }
}
