package org.realverse.youmatter.replicator;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.realverse.youmatter.ModContent;
import org.realverse.youmatter.YMConfig;
import org.realverse.youmatter.util.GeneralUtils;
import org.realverse.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReplicatorBlockEntity extends BlockEntity implements MenuProvider {
    private boolean currentMode = true;  //true = loop; false = one time
    private boolean isActive = false;
    // Current displayed item index -> cachedItems
    private int currentIndex;
    private int currentPartTick; // only execute the following code every 5 ticks
    private ItemStack currentItem;

    private final FluidTank tank;
    private final IFluidHandler fluidHandler;
    public ItemStackHandler inventory;
    private List<ItemStack> cachedItems;
    private static final int MAX_UMATTER = 16000;
    private final MyEnergyStorage myEnergyStorage;

    boolean isCurrentMode() {
        return currentMode;
    }

    public ReplicatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.REPLICATOR_BLOCK_ENTITY.get(), pos, state);
        this.tank = new FluidTank(MAX_UMATTER) {
            @Override
            protected void onContentsChanged() {
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
                setChanged();
            }
        };
        this.fluidHandler = new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return ReplicatorBlockEntity.this.getTank().getFluid();
            }

            @Override
            public int getTankCapacity(int tank) {
                return MAX_UMATTER;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (stack.getFluid().equals(ModContent.UMATTER.get())) {
                    return true;
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (resource.getFluid().equals(ModContent.UMATTER.get())) {
                    if (MAX_UMATTER - ReplicatorBlockEntity.this.getTank().getFluidAmount() < resource.getAmount()) {
                        return tank.fill(new FluidStack(resource.getFluid(), MAX_UMATTER), action);
                    } else {
                        return tank.fill(resource, action);
                    }
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                assert ModContent.UMATTER.get() != null;
                return new FluidStack(ModContent.UMATTER.get(), 0);
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                assert ModContent.UMATTER.get() != null;
                return new FluidStack(ModContent.UMATTER.get(), 0);
            }
        };
        this.inventory = new ItemStackHandler(5) {
            @Override
            public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot == 2) {
                    return stack;
                } else {
                    return super.insertItem(slot, stack, simulate);
                }
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 2) {
                    return ItemStack.EMPTY;
                } else {
                    return super.extractItem(slot, amount, simulate);
                }
            }

            @Override
            protected void onContentsChanged(int slot) {
                ReplicatorBlockEntity.this.setChanged();
            }
        };
        this.currentIndex = 0;
        this.currentPartTick = 0;
        this.myEnergyStorage = new MyEnergyStorage(this, 1000000, 2000);
        this.progress = 0;
    }

    public void setCurrentMode(boolean currentMode) {
        this.currentMode = currentMode;
        setChanged();

        if(level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        setChanged();

        if(level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    FluidTank getTank() {
        return tank;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCapabilities();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ReplicatorBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(currentPartTick == 5) {
            currentPartTick = 0;
           if(inventory != null) {
                if (!inventory.getStackInSlot(3).isEmpty()) {
                    ItemStack item = inventory.getStackInSlot(3);
                    if (item.getItem() instanceof BucketItem && GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), new ItemStack(Items.BUCKET, 1), false)) {
                        IFluidHandlerItem handler = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if(handler != null) {
                            if (!handler.getFluidInTank(0).isEmpty() && handler.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                                if (MAX_UMATTER - getTank().getFluidAmount() >= 1000) {
                                    getTank().fill(new FluidStack(ModContent.UMATTER.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                                    inventory.setStackInSlot(3, ItemStack.EMPTY);
                                    inventory.insertItem(4, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        }
                    } else if(GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), inventory.getStackInSlot(3), false)) {
                        IFluidHandlerItem handler = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if(handler != null) {
                            if (handler.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                                if (handler.getFluidInTank(0).getAmount() > MAX_UMATTER - getTank().getFluidAmount()) { //given fluid is more than what fits in the U-Tank
                                    getTank().fill(handler.drain(MAX_UMATTER - getTank().getFluidAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                } else { //given fluid fits perfectly in U-Tank
                                    getTank().fill(handler.drain(handler.getFluidInTank(0).getAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        }
                        inventory.setStackInSlot(3, ItemStack.EMPTY);
                        inventory.insertItem(4, item, false);
                    }
                }

                ItemStack thumb = inventory.getStackInSlot(0);
                if (thumb.isEmpty()){ //in case user removes thumb drive while replicator is in operation
                    inventory.setStackInSlot(2, ItemStack.EMPTY);
                    cachedItems = null;
                    currentIndex = 0;
                    progress = 0;
                } else {
                    if (thumb.has(DataComponents.CONTAINER)) {
                        ItemContainerContents contents = thumb.get(DataComponents.CONTAINER);
                        if(contents != null) {
                            this.cachedItems = new ArrayList<>();
                            if (cachedItems != null) {
                                for(ItemStack newIS : contents.nonEmptyItems()) {
                                    if (newIS != null) {
                                        cachedItems.add(newIS.copy());
                                    }
                                }
                                renderItem(cachedItems, currentIndex);
                                if(progress == 0) {
                                    if (!inventory.getStackInSlot(2).isEmpty()) {
                                        if (isActive) {
                                            currentItem = cachedItems.get(currentIndex);
                                            if(myEnergyStorage != null) {
                                                if (myEnergyStorage.getEnergyStored() >= YMConfig.get().energyReplicator) {
                                                    if(tank.getFluidAmount() >= GeneralUtils.getUMatterAmountForItem(currentItem.getItem())) {
                                                        tank.drain(GeneralUtils.getUMatterAmountForItem(currentItem.getItem()), IFluidHandler.FluidAction.EXECUTE);
                                                        progress++;
                                                        myEnergyStorage.extractEnergy(YMConfig.get().energyReplicator, false);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if(isActive) {
                                        if(progress >= 100) {
                                            if(!inventory.getStackInSlot(2).isEmpty()) {
                                                if(!currentMode) { //if mode is single run, then pause machine
                                                    isActive = false;
                                                }
                                                inventory.insertItem(1, currentItem, false);
                                            }
                                            progress = 0;
                                        } else {
                                            if (currentItem != null) {
                                                if (!currentItem.isEmpty()) {
                                                    if (ItemStack.isSameItem(currentItem, inventory.getStackInSlot(2))) { // Check if selected item hasn't changed
                                                        if(inventory.getStackInSlot(1).isEmpty() || GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(1), currentItem, false)) { //check if output slot is still empty
                                                            if(myEnergyStorage != null) {
                                                                if (myEnergyStorage.getEnergyStored() >= YMConfig.get().energyReplicator) {
                                                                    progress++;
                                                                    myEnergyStorage.extractEnergy(YMConfig.get().energyReplicator, false);
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        progress = 0; // abort if not
                                                    }
                                                }
                                            } else {
                                                if(cachedItems.get(currentIndex) != null) { //in case the current item isn't loaded yet -> this happens when reloading the world, see issue #31 on GitHub
                                                    currentItem = cachedItems.get(currentIndex);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        currentPartTick++;
    }

    public void renderPrevious() {
        if(cachedItems != null){
            if (currentIndex > 0) {
                currentIndex = currentIndex - 1;
            }
        }

    }

    public void renderNext() {
        if(cachedItems != null){
            if (currentIndex < cachedItems.size() - 1) {
                currentIndex = currentIndex + 1;
            }
        }

    }

    private void renderItem(List<ItemStack> cache, int index) {
        if(index <= cache.size() - 1 && index >= 0) {
            if(cache.get(index) != null) {
                if (inventory != null) {
                    inventory.setStackInSlot(2, cache.get(index));
                }
            }
        }
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
        tank.readFromNBT(provider, compound.getCompound("tank"));
        setEnergy(compound.getInt("energy"));
        setActive(compound.getBoolean("isActive"));
        setProgress(compound.getInt("progress"));
        setCurrentMode(compound.getBoolean("mode"));
        if (compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        CompoundTag tagTank = new CompoundTag();
        tank.writeToNBT(provider, tagTank);
        compound.put("tank", tagTank);
        compound.putInt("energy", getEnergy());
        compound.putBoolean("isActive", isActive());
        compound.putBoolean("mode", isCurrentMode());
        compound.putInt("progress", getProgress());
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

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModContent.REPLICATOR_BLOCK.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new ReplicatorMenu(windowID, level, worldPosition, playerInventory, player);

    }
    public IItemHandler getItemHandler() {
        return this.inventory;
    }

    public IEnergyStorage getEnergyHandler() {
        return this.myEnergyStorage;
    }

    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }
}
