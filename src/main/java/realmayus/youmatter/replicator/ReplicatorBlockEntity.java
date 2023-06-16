package realmayus.youmatter.replicator;


import static realmayus.youmatter.util.GeneralUtils.getUMatterAmountForItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import realmayus.youmatter.ModContent;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.util.GeneralUtils;
import realmayus.youmatter.util.MyEnergyStorage;

public class ReplicatorBlockEntity extends BlockEntity implements MenuProvider {

    public ReplicatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.REPLICATOR_BLOCK_ENTITY.get(), pos, state);
    }


    private boolean currentMode = true;  //true = loop; false = one time

    private boolean isActive = false;

    boolean isCurrentMode() {
        return currentMode;
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
        isActive = active;
        setChanged();

        if(level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private static final int MAX_UMATTER = 10500;

    private FluidTank tank = new FluidTank(MAX_UMATTER) {
        @Override
        protected void onContentsChanged() {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    };

    FluidTank getTank() {
        return tank;
    }

    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override
        public int getTanks() {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return getTank().getFluid();
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
                if (MAX_UMATTER - getTank().getFluidAmount() < resource.getAmount()) {
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
    });

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventory.cast();
        }
        if(cap == ForgeCapabilities.ENERGY) {
            return myEnergyStorage.cast();

        }
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }

        return super.getCapability(cap, side);
    }


    public LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(5) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 2) {
                return stack;
            } else {
                return super.insertItem(slot, stack, simulate);
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
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
    });

    private List<ItemStack> cachedItems;

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventory.invalidate();
        myEnergyStorage.invalidate();
        fluidHandler.invalidate();
    }

    // Current displayed item index -> cachedItems
    private int currentIndex = 0;
    private int currentPartTick = 0; // only execute the following code every 5 ticks
    private ItemStack currentItem;
    public static void tick(Level level, BlockPos pos, BlockState state, ReplicatorBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(currentPartTick == 5) {
            currentPartTick = 0;
            inventory.ifPresent(inventory -> {
                if (!inventory.getStackInSlot(3).isEmpty()) {
                    ItemStack item = inventory.getStackInSlot(3);
                    if (item.getItem() instanceof BucketItem && GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), new ItemStack(Items.BUCKET, 1), false)) {
                        item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(h -> {
                            if (!h.getFluidInTank(0).isEmpty() && h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                                if (MAX_UMATTER - getTank().getFluidAmount() >= 1000) {
                                    getTank().fill(new FluidStack(ModContent.UMATTER.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                                    inventory.setStackInSlot(3, ItemStack.EMPTY);
                                    inventory.insertItem(4, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        });
                    } else if(GeneralUtils.canAddItemToSlot(inventory.getStackInSlot(4), inventory.getStackInSlot(3), false)) {
                        item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(h -> {
                            if (h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                                if (h.getFluidInTank(0).getAmount() > MAX_UMATTER - getTank().getFluidAmount()) { //given fluid is more than what fits in the U-Tank
                                    getTank().fill(h.drain(MAX_UMATTER - getTank().getFluidAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                } else { //given fluid fits perfectly in U-Tank
                                    getTank().fill(h.drain(h.getFluidInTank(0).getAmount(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        });
                        inventory.setStackInSlot(3, ItemStack.EMPTY);
                        inventory.insertItem(4, item, false);
                    }
                }

                ItemStack thumbdrive = inventory.getStackInSlot(0);
                if (thumbdrive.isEmpty()){ //in case user removes thumb drive while replicator is in operation
                    inventory.setStackInSlot(2, ItemStack.EMPTY);
                    cachedItems = null;
                    currentIndex = 0;
                    progress = 0;
                } else {
                    if (thumbdrive.hasTag()) {
                        if(thumbdrive.getTag() != null) {
                            ListTag taglist = (ListTag) thumbdrive.getTag().get("stored_items");
                            cachedItems = new ArrayList<>();
                            if (taglist != null) {
                                for(Tag nbt : taglist) {
                                    if (nbt != null) {
                                        if(nbt instanceof StringTag item) {
                                            ItemStack newitem = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item.getAsString()))), 1);
                                            cachedItems.add(newitem);
                                        }
                                    }
                                }
                                renderItem(cachedItems, currentIndex);
                                if(progress == 0) {
                                    if (!inventory.getStackInSlot(2).isEmpty()) {
                                        if (isActive) {
                                            currentItem = cachedItems.get(currentIndex);
                                            myEnergyStorage.ifPresent(myEnergyStorage -> {
                                                if (myEnergyStorage.getEnergyStored() >= YMConfig.CONFIG.energyReplicator.get()) {
                                                    if(tank.getFluidAmount() >= getUMatterAmountForItem(currentItem.getItem())) {
                                                        tank.drain(getUMatterAmountForItem(currentItem.getItem()), IFluidHandler.FluidAction.EXECUTE);
                                                        progress++;
                                                        myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyReplicator.get(), false);
                                                    }
                                                }
                                            });

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
                                                            myEnergyStorage.ifPresent(myEnergyStorage -> {
                                                                if (myEnergyStorage.getEnergyStored() >= YMConfig.CONFIG.energyReplicator.get()) {
                                                                    progress++;
                                                                    myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyReplicator.get(), false);
                                                                }
                                                            });
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
            });
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
                inventory.ifPresent(inventory -> inventory.setStackInSlot(2, cache.get(index)));
            }
        }
    }

    private LazyOptional<MyEnergyStorage> myEnergyStorage = LazyOptional.of(() -> new MyEnergyStorage(this, 1000000, 2000));

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

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        tank.readFromNBT(compound.getCompound("tank"));
        setEnergy(compound.getInt("energy"));
        setActive(compound.getBoolean("isActive"));
        setProgress(compound.getInt("progress"));
        setCurrentMode(compound.getBoolean("mode"));
        if (compound.contains("inventory")) {
            inventory.resolve().get().deserializeNBT((CompoundTag) compound.get("inventory"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        CompoundTag tagTank = new CompoundTag();
        tank.writeToNBT(tagTank);
        compound.put("tank", tagTank);
        compound.putInt("energy", getEnergy());
        compound.putBoolean("isActive", isActive);
        compound.putBoolean("mode", isCurrentMode());
        compound.putInt("progress", getProgress());
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

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModContent.REPLICATOR_BLOCK.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new ReplicatorMenu(windowID, level, worldPosition, playerInventory, player);

    }
}
