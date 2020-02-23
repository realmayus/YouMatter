package realmayus.youmatter.replicator;


import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.util.CustomInvWrapper;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static realmayus.youmatter.util.GeneralUtils.getUMatterAmountForItem;

public class ReplicatorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public ReplicatorTile() {
        super(ObjectHolders.REPLICATOR_TILE);
    }


    private boolean currentMode = true;  //true = loop; false = one time

    private boolean currentClientMode = false;

    private boolean isActive = false;

    private boolean isActiveClient = false;

    boolean isCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(boolean currentMode) {
        this.currentMode = currentMode;
    }

    boolean isCurrentClientMode() {
        return currentClientMode;
    }

    void setCurrentClientMode(boolean currentClientMode) {
        this.currentClientMode = currentClientMode;
    }

    boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    boolean isActiveClient() {
        return isActiveClient;
    }

    void setActiveClient(boolean activeClient) {
        isActiveClient = activeClient;
    }

    private static final int MAX_UMATTER = 10500;

    private FluidTank tank = new FluidTank(MAX_UMATTER) {
        @Override
        protected void onContentsChanged() {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    FluidTank getTank() {
        return tank;
    }

    private IFluidHandler fluidHandler = new IFluidHandler() {
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
            if (stack.getFluid().equals(ModFluids.UMATTER.get())) {
                return true;
            }
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid().equals(ModFluids.STABILIZER.get())) {
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
            assert ModFluids.UMATTER.get() != null;
            return new FluidStack(ModFluids.UMATTER.get(), 0);
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            assert ModFluids.UMATTER.get() != null;
            return new FluidStack(ModFluids.UMATTER.get(), 0);
        }
    };

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> invWrapper).cast();
        }
        if(cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> myEnergyStorage).cast();

        }
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> fluidHandler).cast();
        }

        return super.getCapability(cap, side);
    }


    public ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            ReplicatorTile.this.markDirty();
        }
    };

    CustomInvWrapper invWrapper = new CustomInvWrapper(inventory);

    private List<ItemStack> cachedItems;

    // Current displayed item index -> cachedItems
    private int currentIndex = 0;
    private int currentPartTick = 0; // only execute the following code every 5 ticks
    private ItemStack currentItem;
    @Override
    public void tick() {
        if(currentPartTick == 5) {
            currentPartTick = 0;
            //only execute this code on the server
            if(!world.isRemote) {
                // Fill tank through inserted buckets
                if (!this.inventory.getStackInSlot(3).isEmpty()) {
                    if(this.inventory.getStackInSlot(3).getItem() instanceof BucketItem) {
                        BucketItem bucket = (BucketItem) this.inventory.getStackInSlot(3).getItem();
                        if(bucket.getFluid() != null) {
                            if (bucket.getFluid().getFluid().equals(ModFluids.UMATTER.get())) {
                                if (getTank().getFluidAmount() + 1000 < getTank().getCapacity()) {
                                    getTank().fill(new FluidStack(ModFluids.UMATTER.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                                    this.inventory.setStackInSlot(3, ItemStack.EMPTY);
                                    this.inventory.insertItem(4, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        }
                    }
                }

                ItemStack thumbdrive = inventory.getStackInSlot(0);
                if (thumbdrive.isEmpty()){
                    if(progress > 0) {
                        if (currentItem != null) {
                            if(!currentItem.isEmpty()) {
                                getTank().fill(new FluidStack(ModFluids.UMATTER.get(), getUMatterAmountForItem(currentItem.getItem())), IFluidHandler.FluidAction.EXECUTE); // give the user its umatter back!
                            }
                        }
                    }
                    inventory.setStackInSlot(2, ItemStack.EMPTY);
                    cachedItems = null;
                    currentIndex = 0;
                    progress = 0;
                } else {
                    if (thumbdrive.hasTag()) {
                        if(thumbdrive.getTag() != null) {
                            ListNBT taglist = (ListNBT) thumbdrive.getTag().get("stored_items");
                            cachedItems = new ArrayList<>();
                            if (taglist != null) {
                                for(INBT nbt : taglist) {
                                    if (nbt != null) {
                                        if(nbt instanceof StringNBT) {
                                            StringNBT item = (StringNBT) nbt;
                                            ItemStack newitem = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item.getString()))), 1);
                                            cachedItems.add(newitem);
                                        }
                                    }
                                }
                                renderItem(cachedItems, currentIndex);
                                if(progress == 0) {
                                    if (!inventory.getStackInSlot(2).isEmpty()) {
                                        if(inventory.getStackInSlot(1).isEmpty()) {
                                            if (isActive) {
                                                currentItem = cachedItems.get(currentIndex);
                                                if(tank.getFluidAmount() >= getUMatterAmountForItem(currentItem.getItem())) {
                                                    tank.drain(getUMatterAmountForItem(currentItem.getItem()), IFluidHandler.FluidAction.EXECUTE);
                                                    progress++;
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
                                                inventory.setStackInSlot(1, currentItem);
                                            }
                                            progress = 0;
                                        } else {
                                            if (currentItem != null) {
                                                if (!currentItem.isEmpty()) {
                                                    if (currentItem.isItemEqual(inventory.getStackInSlot(2))) { // Check if selected item hasn't changed
                                                        if(inventory.getStackInSlot(1).isEmpty()) { //check if output slot is still empty
                                                            progress++;
                                                        }
                                                    } else {
                                                        if (progress > 0) { // progress was over 0 (= already drained U-matter) and then aborted
                                                            getTank().fill(new FluidStack(ModFluids.UMATTER.get(), getUMatterAmountForItem(currentItem.getItem())), IFluidHandler.FluidAction.EXECUTE); // give the user its umatter back!
                                                        }
                                                        progress = 0; // abort if not
                                                    }
                                                }
                                            } else {
                                                if(cachedItems.get(currentIndex) != null) { //in case the current item isn't loaded yet -> this happens when reloading the world, see issue #31 on GitHub
                                                    currentItem = cachedItems.get(currentIndex);
                                                }
                                            }
                                        }

                                        myEnergyStorage.consumePower(2048);
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
                inventory.setStackInSlot(2, cache.get(index));
            }
        }
    }

    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, 2000);

    private int clientEnergy = -1;

    private int clientProgress = -1;
    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    int getClientProgress() {
        return clientProgress;
    }

    void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }

    int getClientEnergy() {
        return clientEnergy;
    }

    void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        tank.readFromNBT(compound.getCompound("tank"));
        myEnergyStorage.setEnergy(compound.getInt("energy"));
        setActive(compound.getBoolean("isActive"));
        setProgress(compound.getInt("progress"));
        setCurrentMode(compound.getBoolean("mode"));
        if (compound.contains("inventory")) {
            inventory.deserializeNBT((CompoundNBT) compound.get("inventory"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        CompoundNBT tagTank = new CompoundNBT();
        tank.writeToNBT(tagTank);
        compound.put("tank", tagTank);
        compound.putInt("energy", getEnergy());
        compound.putBoolean("isActive", isActive);
        compound.putBoolean("mode", isCurrentMode());
        compound.putInt("progress", getProgress());
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT());
        }
        return compound;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ObjectHolders.REPLICATOR_BLOCK.getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ReplicatorContainer(windowID, world, pos, playerInventory, playerEntity);

    }
}
