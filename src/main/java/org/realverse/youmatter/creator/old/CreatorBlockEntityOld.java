package org.realverse.youmatter.creator.old;


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
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.realverse.youmatter.ModContent;
import org.realverse.youmatter.YMConfig;
import org.realverse.youmatter.replicator.ReplicatorBlockEntity;
import org.realverse.youmatter.util.GeneralUtils;
import org.realverse.youmatter.util.MyEnergyStorage;
import org.realverse.youmatter.util.tags.Fluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreatorBlockEntityOld extends BlockEntity implements MenuProvider {

    private static final int MAX_UMATTER = 16000;
    private static final int MAX_STABILIZER = 16000;

    private boolean isActive = false;
    private boolean currentMode = false;

    public ItemStackHandler inventory;
    private FluidTank uTank;
    private FluidTank sTank;
    private IFluidHandler fluidHandler;
    private MyEnergyStorage myEnergyStorage;

    boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        setChanged();

        if(level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    boolean isCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(boolean mode) {
        this.currentMode = mode;
        setChanged();

        if(level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public CreatorBlockEntityOld(BlockPos pos, BlockState state) {
        super(ModContent.CREATOR_BLOCK_ENTITY_OLD.get(), pos, state);
        this.inventory = new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                CreatorBlockEntityOld.this.setChanged();
            }
        };
        this.uTank = new FluidTank(this.MAX_UMATTER) {
            @Override
            protected void onContentsChanged() {
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 3);
                setChanged();
            }
        };
        this.sTank = new FluidTank(MAX_STABILIZER) {
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
                return 2;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                if (tank == 0) {
                    return uTank.getFluid();
                } else if (tank == 1) {
                    return sTank.getFluid();
                }
                return null;
            }

            @Override
            public int getTankCapacity(int tank) {
                if (tank == 0) {
                    return MAX_UMATTER;
                } else if (tank == 1) {
                    return MAX_STABILIZER;
                }
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                if (tank == 0) {
                    return false;
                } else if (tank == 1 && stack.getFluid().is(Fluids.STABILIZER)) {
                    return true;
                }
                return false;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (resource.getFluid().is(Fluids.STABILIZER)) {
                    if (MAX_STABILIZER - CreatorBlockEntityOld.this.getSTank().getFluidAmount() < resource.getAmount()) {
                        return sTank.fill(new FluidStack(resource.getFluid(), MAX_STABILIZER), action);
                    } else {
                        return sTank.fill(resource, action);
                    }
                }
                return 0;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.getFluid().equals(ModContent.UMATTER.get())) {
                    if (uTank.getFluidAmount() < resource.getAmount()) {
                        uTank.drain(uTank.getFluid(), action);
                        return uTank.getFluid();
                    } else {
                        uTank.drain(resource, action);
                        return resource;
                    }
                }
                return null;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if(uTank.getFluid().getFluid() != null) {
                    return uTank.drain(uTank.getFluid(), action);
                } else {
                    return null;
                }
            }
        };
        this.myEnergyStorage = new MyEnergyStorage(this, 1000000,Integer.MAX_VALUE);
    }

    FluidTank getUTank() {
        return uTank;
    }

    FluidTank getSTank() {
        return sTank;
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    public void setEnergy(int energy) {
        myEnergyStorage.setEnergy(energy);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCapabilities();
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        this.uTank.readFromNBT(provider, compound.getCompound("uTank"));
        this.sTank.readFromNBT(provider, compound.getCompound("sTank"));
        setEnergy(compound.getInt("energy"));
        setActive(compound.getBoolean("isActive"));
        setCurrentMode(compound.getBoolean("mode"));
        if(compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }
    }


    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        CompoundTag tagSTank = new CompoundTag();
        CompoundTag tagUTank = new CompoundTag();
        sTank.writeToNBT(provider, tagSTank);
        uTank.writeToNBT(provider, tagUTank);
        compound.put("uTank", tagUTank);
        compound.put("sTank", tagSTank);
        compound.putInt("energy", getEnergy());
        compound.putBoolean("isActive", isActive());
        compound.putBoolean("mode", isCurrentMode());
        if(compound.contains("inventory")) {
            inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private int currentPartTick = 0;
    public static void tick(Level level, BlockPos pos, BlockState state, CreatorBlockEntityOld be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (currentPartTick == 40) { // 2 sec
            if(isActive()) {
                if (getEnergy() >= 0.3f * 1000000 && sTank.getFluidAmount() >= 125) { // if energy more than 30 % of max energy
                    if (uTank.getFluidAmount() + YMConfig.get().productionPerTick <= MAX_UMATTER) {
                        sTank.drain(125, IFluidHandler.FluidAction.EXECUTE);
                        uTank.fill(new FluidStack(ModContent.UMATTER.get(), YMConfig.get().productionPerTick), IFluidHandler.FluidAction.EXECUTE);
                        myEnergyStorage.extractEnergy(Math.round(getEnergy()/3f), false);
                    }
                }
            }
            //Auto-outputting U-Matter
            Object[] neighborTE = getNeighborTileEntity(pos);
            if (neighborTE != null) {
                if(isCurrentMode()) {
                    IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, (BlockPos) neighborTE[0], (Direction) neighborTE[1]);
                    if (handler != null) {
                        this.uTank.drain(handler.fill(new FluidStack(ModContent.UMATTER.get(), Math.min(this.uTank.getFluidAmount(), 500)), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
            currentPartTick = 0;
        } else if ((currentPartTick % 5) == 0) { // every five ticks
            if(inventory != null) {
                if (!(inventory.getStackInSlot(3).isEmpty()) && GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), inventory.getStackInSlot(3), false)) {
                    ItemStack item = inventory.getStackInSlot(3);
                    if (item.getItem() instanceof BucketItem) {
                        if (getUTank().getFluidAmount() >= 1000) {
                            getUTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                            inventory.setStackInSlot(3, ItemStack.EMPTY);
                            inventory.insertItem(4, new ItemStack(ModContent.UMATTER_BUCKET.get(), 1), false);
                        }
                    } else {
                        IFluidHandlerItem handler = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if(handler != null) {
                            if (handler.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get()) || handler.getFluidInTank(0).isEmpty()) {
                                if (handler.getTankCapacity(0) - handler.getFluidInTank(0).getAmount() < getUTank().getFluidAmount()) { //fluid in S-Tank is more than what fits in the item's tank
                                    getUTank().drain(handler.fill(new FluidStack(ModContent.UMATTER.get(), handler.getTankCapacity(0) - handler.getFluidInTank(0).getAmount()), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                } else { //S-Tank's fluid fits perfectly in item's tank
                                    getUTank().drain(handler.fill(getUTank().getFluid(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        }
                        inventory.setStackInSlot(3, ItemStack.EMPTY);
                        inventory.insertItem(4, item, false);
                    }
                }
                if (!inventory.getStackInSlot(1).isEmpty()) {
                    ItemStack item = inventory.getStackInSlot(1);
                    if (item.getItem() instanceof BucketItem && GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(2), new ItemStack(Items.BUCKET, 1), false)) {
                        IFluidHandlerItem handler = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if(handler != null) {
                            if (!handler.getFluidInTank(0).isEmpty() && (handler.getFluidInTank(0).getFluid().is(Fluids.STABILIZER))) {
                                if (MAX_STABILIZER - getSTank().getFluidAmount() >= 1000) {
                                    getSTank().fill(new FluidStack(ModContent.STABILIZER.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                                    inventory.setStackInSlot(1, ItemStack.EMPTY);
                                    inventory.insertItem(2, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        }
                    } else if(GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(2), inventory.getStackInSlot(1), false)) {
                        IFluidHandlerItem handler = item.getCapability(Capabilities.FluidHandler.ITEM);
                        if(handler != null) {
                            if (handler.getFluidInTank(0).getFluid().is(Fluids.STABILIZER)) {
                                if (handler.getFluidInTank(0).getAmount() > MAX_STABILIZER - getSTank().getFluidAmount()) { //given fluid is more than what fits in the S-Tank
                                    getSTank().fill(handler.drain(MAX_STABILIZER - getSTank().getFluidAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                } else { //given fluid fits perfectly in S-Tank
                                    getSTank().fill(handler.drain(handler.getFluidInTank(0).getAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        }
                        inventory.setStackInSlot(1, ItemStack.EMPTY);
                        inventory.insertItem(2, item, false);
                    }
                }
            }
            currentPartTick++;
        } else {
            currentPartTick++;
        }
    }

    private Object[] getNeighborTileEntity(BlockPos pos) {
        Object[] result = null;

        for(Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockEntity offsetBe = this.level.getBlockEntity(offsetPos);
            if (offsetBe != null) {
                if (offsetBe instanceof ReplicatorBlockEntity) {
                    return new Object[]{offsetPos, facing};
                }

                IFluidHandler h = this.level.getCapability(Capabilities.FluidHandler.BLOCK, offsetPos, facing);
                if (h != null && h.fill(new FluidStack(ModContent.UMATTER.get(), 500), IFluidHandler.FluidAction.SIMULATE) > 0 && result == null) {
                    result = new Object[]{offsetPos, facing};
                }
            }
        }

        return result;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModContent.CREATOR_BLOCK_OLD.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player playerEntity) {
        return new CreatorMenuOld(windowID, level, worldPosition, playerInventory, playerEntity);
    }

    public ItemStackHandler getItemHandler() {
        return this.inventory;
    }

    public IEnergyStorage getEnergyHandler() {
        return this.myEnergyStorage;
    }

    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }
}
