package realmayus.youmatter.replicator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.util.CustomInvWrapper;
import realmayus.youmatter.util.GeneralUtils;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static realmayus.youmatter.util.GeneralUtils.getUMatterAmountForItem;

public class TileReplicator extends TileEntity implements  ITickable{
    public TileReplicator() {
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
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    FluidTank getTank() {
        return tank;
    }

    /**
     * If we are too far away from this tile entity you cannot use it
     */
    boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }
    private IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public IFluidTankProperties[] getTankProperties() {
            IFluidTankProperties[] fluidTankProperties = new IFluidTankProperties[1];
            fluidTankProperties[0] = new FluidTankProperties(tank.getFluid(), MAX_UMATTER, true, false);
            return fluidTankProperties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (resource.getFluid().equals(ModFluids.UMATTER)) {
                if (MAX_UMATTER - getTank().getFluidAmount() < resource.amount) {
                    return tank.fill(new FluidStack(resource.getFluid(), MAX_UMATTER), doFill);
                } else {
                    return tank.fill(resource, doFill);
                }
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }

    };
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }

        if(capability == CapabilityEnergy.ENERGY) {
            return true;
        }

        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);


    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(combinedHandler);
        }

        if(capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(myEnergyStorage);

        }

        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler);
        }

        return super.getCapability(capability, facing);
    }


    /**
     * Handler for the Input Slots
     */
    private ItemStackHandler inputHandler = new ItemStackHandler(5) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileReplicator.this.markDirty();
        }
    };

    /**
     * Handler for the Output Slots
     */
    private ItemStackHandler outputHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            TileReplicator.this.markDirty();
        }
    };

    private CustomInvWrapper combinedHandler = new CustomInvWrapper(inputHandler, outputHandler);

    private List<ItemStack> cachedItems;

    // Current displayed item index -> cachedItems
    private int currentIndex = 0;
    private int currentPartTick = 0; // only execute the following code every 5 ticks
    private ItemStack currentItem;
    @Override
    public void update() {
        if(currentPartTick == 5) {
            currentPartTick = 0;
            //only execute this code on the server
            if(!world.isRemote) {
//                if (!this.inputHandler.getStackInSlot(3).isEmpty()) {
//                    if(this.inputHandler.getStackInSlot(3).getItem() instanceof UniversalBucket) {
//                        UniversalBucket bucket = (UniversalBucket) this.inputHandler.getStackInSlot(3).getItem();
//                        if(bucket.getFluid(this.inputHandler.getStackInSlot(3)) != null) {
//                            if (bucket.getFluid(this.inputHandler.getStackInSlot(3)).getFluid().equals(ModFluids.UMATTER)) {
//                                if (getTank().getFluidAmount() + 1000 < getTank().getCapacity()) {
//                                    getTank().fill(new FluidStack(ModFluids.UMATTER, 1000), true);
//                                    this.inputHandler.setStackInSlot(3, ItemStack.EMPTY);
//                                    this.combinedHandler.insertItem(4, new ItemStack(Items.BUCKET, 1), false);
//                                }
//                            }
//                        }
//                    }
//                }

                if (!this.inputHandler.getStackInSlot(3).isEmpty()) {
                    ItemStack item = this.inputHandler.getStackInSlot(3);
                    if (item.getItem() instanceof UniversalBucket && GeneralUtils.canAddItemToSlot(this.inputHandler.getStackInSlot(4), new ItemStack(Items.BUCKET, 1), false, false)) {
                        if (item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                            IFluidTankProperties tankProperties = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()[0];
                            if (tankProperties.getContents() != null) {
                                if (tankProperties.getContents().getFluid().equals(ModFluids.UMATTER)) {
                                    if (MAX_UMATTER - getTank().getFluidAmount() >= 1000) {
                                        if (MAX_UMATTER - getTank().getFluidAmount() >= 1000) {
                                            getTank().fill(new FluidStack(ModFluids.UMATTER, 1000), true);
                                            this.inputHandler.setStackInSlot(3, ItemStack.EMPTY);
                                            this.inputHandler.insertItem(4, new ItemStack(Items.BUCKET, 1), false);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (!(item.getItem() instanceof UniversalBucket) && GeneralUtils.canAddItemToSlot(this.inputHandler.getStackInSlot(4), this.inputHandler.getStackInSlot(3), false, false)) {
                        if (item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                            IFluidTankProperties tankProperties = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()[0];
                            if (tankProperties.getContents() != null) {
                                if (tankProperties.getContents().getFluid().equals(ModFluids.UMATTER)) {
                                    if (tankProperties.getContents().amount > MAX_UMATTER - getTank().getFluidAmount()) { //given fluid is more than what fits in the U-Tank
                                        getTank().fill(item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(MAX_UMATTER - getTank().getFluidAmount(), true), true);
                                    } else { //given fluid fits perfectly in U-Tank
                                        getTank().fill(item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(tankProperties.getContents().amount, true), true);
                                    }
                                }
                            }

                        }
                        this.inputHandler.setStackInSlot(3, ItemStack.EMPTY);
                        this.inputHandler.insertItem(4, item, false);
                    }
                }


                ItemStack thumbdrive = inputHandler.getStackInSlot(0);
                if (thumbdrive.isEmpty()){
                    if(progress > 0) {
                        if (currentItem != null) {
                            if(!currentItem.isEmpty()) {
                                getTank().fill(new FluidStack(ModFluids.UMATTER, getUMatterAmountForItem(currentItem.getItem())), true); // give the user its umatter back!
                            }
                        }
                    }
                    combinedHandler.setStackInSlot(2, ItemStack.EMPTY);
                    cachedItems = null;
                    currentIndex = 0;
                    progress = 0;
                } else {
                    if (thumbdrive.hasTagCompound()) {
                        if(thumbdrive.getTagCompound() != null) {
                            if(thumbdrive.getTagCompound().hasKey("stored_items")) {
                                NBTTagList taglist = (NBTTagList) thumbdrive.getTagCompound().getTag("stored_items");
                                cachedItems = new ArrayList<>();
                                for(NBTBase nbt : taglist) {
                                    if (nbt != null) {
                                        if(nbt instanceof NBTTagString) {
                                            NBTTagString item = (NBTTagString) nbt;
                                            String registry_name = item.getString().substring(0, item.getString().indexOf("|"));
                                            int metadata = Integer.parseInt(item.getString().substring(item.getString().indexOf("|") + 1));
                                            ItemStack newitem = new ItemStack(Objects.requireNonNull(Item.getByNameOrId(registry_name)), 1, metadata);
                                            cachedItems.add(newitem);
                                        }
                                    }
                                }
                                renderItem(cachedItems, currentIndex);
                                if(progress == 0) {
                                    if (!combinedHandler.getStackInSlot(2).isEmpty()) {
                                        if (isActive) {
                                            currentItem = cachedItems.get(currentIndex);
                                            if(tank.getFluidAmount() >= getUMatterAmountForItem(currentItem.getItem())) {
                                                tank.drain(getUMatterAmountForItem(currentItem.getItem()), true);
                                                progress++;
                                            }
                                        }
                                    }
                                } else {
                                    if(isActive) {
                                        if(progress >= 100) {
                                            if(!combinedHandler.getStackInSlot(2).isItemEqual(ItemStack.EMPTY)) {
                                                if(!currentMode) { //if mode is single run, then pause machine
                                                    isActive = false;
                                                }
                                                combinedHandler.insertItem(1, currentItem, false);
                                            }
                                            progress = 0;
                                        } else {
                                            if (currentItem != null) {
                                                if (!currentItem.isEmpty()) {
                                                    if (currentItem.isItemEqual(combinedHandler.getStackInSlot(2))) { // Check if selected item hasn't changed
                                                        if(combinedHandler.getStackInSlot(1).isEmpty() || GeneralUtils.canAddItemToSlot(combinedHandler.getStackInSlot(1), currentItem, false, false)) { //check if output slot is still empty
                                                            progress++;
                                                        }
                                                    } else {
                                                        if (progress > 0) { // progress was over 0 (= already drained U-matter) and then aborted
                                                            getTank().fill(new FluidStack(ModFluids.UMATTER, getUMatterAmountForItem(currentItem.getItem())), true); // give the user its umatter back!
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
                                        myEnergyStorage.consumePower(YMConfig.energyReplicator);
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
                combinedHandler.setStackInSlot(2, cache.get(index));
            }
        }
    }

    int getClientFluidAmount() {
        return clientFluidAmount;
    }

    void setClientFluidAmount(int clientFluidAmount) {
        this.clientFluidAmount = clientFluidAmount;
    }

    private int clientFluidAmount = -1;

    void setClientTank(NBTTagCompound tank) {
        FluidTank newTank = new FluidTank(10000).readFromNBT(tank);
        getTank().setFluid(newTank.getFluid());
        getTank().setCapacity(newTank.getCapacity());
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
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("tank")){
            tank.readFromNBT(compound.getCompoundTag("tank"));
        }
        if (compound.hasKey("energy")){
            myEnergyStorage.setEnergy(compound.getInteger("energy"));
        }
        if (compound.hasKey("isActive")) {
            setActive(compound.getBoolean("isActive"));
        }
        if (compound.hasKey("progress")) {
            setProgress(compound.getInteger("progress"));
        }
        if (compound.hasKey("mode")) {
            setCurrentMode(compound.getBoolean("mode"));
        }
        if (compound.hasKey("itemsIN")) {
            inputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsIN"));
        }
        if (compound.hasKey("itemsOUT")) {
            outputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsOUT"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound tagTank = new NBTTagCompound();
        tank.writeToNBT(tagTank);
        compound.setTag("tank", tagTank);
        compound.setInteger("energy", getEnergy());
        compound.setBoolean("isActive", isActive);
        compound.setBoolean("mode", isCurrentMode());
        compound.setInteger("progress", getProgress());
        if (inputHandler != null) {
            if (inputHandler.serializeNBT() != null) {
                compound.setTag("itemsIN", inputHandler.serializeNBT());
            }
        }
        if (outputHandler != null) {
            if (outputHandler.serializeNBT() != null) {
                compound.setTag("itemsOUT", outputHandler.serializeNBT());
            }
        }
        return compound;
    }
}
