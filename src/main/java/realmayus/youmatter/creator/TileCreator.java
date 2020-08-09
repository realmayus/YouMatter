package realmayus.youmatter.creator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.replicator.TileReplicator;
import realmayus.youmatter.util.GeneralUtils;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class TileCreator extends TileEntity implements  ITickable{

    public TileCreator() {
    }

    private static final int MAX_UMATTER = 11000;
    private static final int MAX_STABILIZER = 11000;

    private boolean isActivatedClient = true;
    private boolean isActivated = true;

    boolean isActivatedClient() {
        return isActivatedClient;
    }

    void setActivatedClient(boolean activatedClient) {
        isActivatedClient = activatedClient;
    }

    boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }



    private FluidTank uTank = new FluidTank(MAX_UMATTER) {
        @Override
        protected void onContentsChanged() {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    private FluidTank sTank = new FluidTank(MAX_STABILIZER) {
        @Override
        protected void onContentsChanged() {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    FluidTank getUTank() {
        return uTank;

    }

    FluidTank getSTank() {
        return sTank;
    }

    private IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public IFluidTankProperties[] getTankProperties() { // Need this in order for mechanism pipes to work
            IFluidTankProperties[] fluidTankProperties = new IFluidTankProperties[2];
            fluidTankProperties[1] = new FluidTankProperties(uTank.getFluid(), MAX_UMATTER, false, true);
            fluidTankProperties[0] = new FluidTankProperties(sTank.getFluid(), MAX_STABILIZER, true, false);
            return fluidTankProperties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (resource.getFluid().equals(ModFluids.STABILIZER) || resource.getFluid().getName().equalsIgnoreCase(YMConfig.alternativeStabilizer)) {
                if (MAX_STABILIZER - getSTank().getFluidAmount() < resource.amount) {
                    return sTank.fill(new FluidStack(resource.getFluid(), MAX_STABILIZER), doFill);
                } else {
                    return sTank.fill(resource, doFill);
                }
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (resource.getFluid().equals(ModFluids.UMATTER)) {
                if (uTank.getFluidAmount() < resource.amount) {
                    uTank.drain(uTank.getFluid(), doDrain);
                    return uTank.getFluid();
                } else {
                    uTank.drain(resource, doDrain);
                    return resource;
                }
            }
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if(uTank.getFluid() != null) {
                if(uTank.getFluid().getFluid() != null) {
                    return uTank.drain(uTank.getFluid(), doDrain);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

    };

    /**
     * If we are too far away from this tile entity you cannot use it
     */
    boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

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

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
            TileCreator.this.markDirty();
        }
    };




    /**
     * Handler for the Output Slots
     */
    private ItemStackHandler outputHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            TileCreator.this.markDirty();
        }
    };

    private CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler, outputHandler);

    public int getClientProgress() {
        return clientProgress;
    }

    void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }



    void setClientUFluidAmount(int clientUFluidAmount) {
        this.clientUFluidAmount = clientUFluidAmount;
    }
    void setClientSFluidAmount(int clientSFluidAmount) {
        this.clientSFluidAmount = clientSFluidAmount;
    }

    private int clientUFluidAmount = -1;
    private int clientSFluidAmount = -1;

    int getClientEnergy() {
        return clientEnergy;
    }

    void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    public void setClientTank(NBTTagCompound tank) {
        FluidTank newUTank = new FluidTank(MAX_UMATTER).readFromNBT(tank);
        FluidTank newSTank = new FluidTank(MAX_STABILIZER).readFromNBT(tank);
        getUTank().setFluid(newUTank.getFluid());
        getSTank().setFluid(newSTank.getFluid());
    }


    private int clientEnergy = -1;
    private int clientProgress = -1;

    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, Integer.MAX_VALUE);

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("uTank")) {
            NBTTagCompound tagUTank = compound.getCompoundTag("uTank");
            uTank.readFromNBT(tagUTank);
        }
        if (compound.hasKey("sTank")) {
            NBTTagCompound tagSTank = compound.getCompoundTag("sTank");
            sTank.readFromNBT(tagSTank);
        }
        if (compound.hasKey("energy")) {
            myEnergyStorage.setEnergy(compound.getInteger("energy"));
        }
        if (compound.hasKey("isActivated")) {
            isActivated = compound.getBoolean("isActivated");
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
        NBTTagCompound tagSTank = new NBTTagCompound();
        NBTTagCompound tagUTank = new NBTTagCompound();
        sTank.writeToNBT(tagSTank);
        uTank.writeToNBT(tagUTank);
        compound.setTag("uTank", tagUTank);
        compound.setTag("sTank", tagSTank);
        compound.setInteger("energy", getEnergy());
        compound.setBoolean("isActivated", isActivated);
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

    void setClientUTank(NBTTagCompound tank) {
        FluidTank newTank = new FluidTank(10500).readFromNBT(tank);
        getUTank().setFluid(newTank.getFluid());
        getUTank().setCapacity(newTank.getCapacity());
    }

    void setClientSTank(NBTTagCompound tank) {
        FluidTank newTank = new FluidTank(10500).readFromNBT(tank);
        getSTank().setFluid(newTank.getFluid());
        getSTank().setCapacity(newTank.getCapacity());
    }

    private int currentPartTick = 0;
    @Override
    public void update() {
        if (currentPartTick == 40) { // every 2 sec
            if (!world.isRemote) {
                if(isActivated()) {
                    if (getEnergy() >= 0.3f * 1000000 && sTank.getFluidAmount() >= 125) { // if energy more than 30 % of max energy
                        if (uTank.getFluidAmount() + YMConfig.productionPerWorkcycle <= MAX_UMATTER) {
                            sTank.drain(125, true);
                            uTank.fill(new FluidStack(ModFluids.UMATTER, YMConfig.productionPerWorkcycle), true);
                            myEnergyStorage.consumePower(Math.round(getEnergy()/3f));
                        }
                    }
                }

                //Auto-outputting U-Matter
                Object[] neighborTE = getNeighborTileEntity(pos);
                if(neighborTE != null){
                    if (uTank.getFluidAmount() >= 500) {
                        uTank.drain(Objects.requireNonNull(world.getTileEntity((BlockPos)neighborTE[0])).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, (EnumFacing)neighborTE[1]).fill(new FluidStack(ModFluids.UMATTER, 500), true), true);
                    } else {
                        uTank.drain(Objects.requireNonNull(world.getTileEntity((BlockPos)neighborTE[0])).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, (EnumFacing)neighborTE[1]).fill(new FluidStack(ModFluids.UMATTER, uTank.getFluidAmount()), true), true);
                    }
                }
            }
            currentPartTick = 0;
        } else if ((currentPartTick % 5) == 0) { // every five ticks
            if (!world.isRemote) {
                if (!(this.inputHandler.getStackInSlot(3).isEmpty()) && GeneralUtils.canAddItemToSlot(this.inputHandler.getStackInSlot(4), this.inputHandler.getStackInSlot(3), false, false)) {
                    ItemStack item = this.inputHandler.getStackInSlot(3);
                    if (item.getItem() instanceof ItemBucket) {
                        if (getUTank().getFluidAmount() >= 1000) {
                            getUTank().drain(1000, true);
                            this.inputHandler.setStackInSlot(3, ItemStack.EMPTY);
                            this.combinedHandler.insertItem(4, UniversalBucket.getFilledBucket(new UniversalBucket(), ModFluids.UMATTER), false);
                        }
                    } else {
                        for (int i = 0; i <= item.getCount(); i++) {
//                            ItemStack currentSplit = item.splitStack(1);
                            ItemStack currentSplit = item.copy();
                            item.setCount(item.getCount() - 1);
                            currentSplit.setCount(1);

                            if (currentSplit.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                                IFluidTankProperties tankProperties = currentSplit.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()[0];
                                if (tankProperties.getContents() != null) {
                                    if (tankProperties.getContents().getFluid().equals(ModFluids.UMATTER) || tankProperties.getContents().getFluid() == null) {
                                        if (tankProperties.getCapacity() - tankProperties.getContents().amount < getUTank().getFluidAmount()) { //fluid in S-Tank is more than what fits in the item's tank
                                            getUTank().drain(currentSplit.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).fill(new FluidStack(ModFluids.UMATTER, tankProperties.getCapacity() - tankProperties.getContents().amount), true), true);
                                        } else { //S-Tank's fluid fits perfectly in item's tank
                                            getUTank().drain(currentSplit.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).fill(getUTank().getFluid(), true), true);
                                        }
                                    }
                                }
                            }
                            this.inputHandler.setStackInSlot(3, ItemStack.EMPTY);
                            this.inputHandler.insertItem(4, currentSplit, false);
                        }

                    }
                }
                if (!this.inputHandler.getStackInSlot(1).isEmpty()) {
                    ItemStack item = this.inputHandler.getStackInSlot(1);
                    if (item.getItem() instanceof UniversalBucket && GeneralUtils.canAddItemToSlot(this.inputHandler.getStackInSlot(2), new ItemStack(Items.BUCKET, 1), false, false)) {
                        if (item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                            IFluidTankProperties tankProperties = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()[0];
                            if (tankProperties.getContents() != null && (tankProperties.getContents().getFluid().equals(ModFluids.STABILIZER) || tankProperties.getContents().getFluid().getName().equalsIgnoreCase(YMConfig.alternativeStabilizer))) {
                                if (MAX_STABILIZER - getSTank().getFluidAmount() >= 1000) {
                                    getSTank().fill(new FluidStack(tankProperties.getContents().getFluid(), 1000), true);
                                    this.inputHandler.setStackInSlot(1, ItemStack.EMPTY);
                                    this.inputHandler.insertItem(2, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        }
                    } else if (GeneralUtils.canAddItemToSlot(this.inputHandler.getStackInSlot(2), this.inputHandler.getStackInSlot(1), false, false)) {
                        ItemStack currentSplit = item.copy();
                        item.setCount(item.getCount() - 1);
                        currentSplit.setCount(1);

                        System.out.println("Count of 'item': " + item.getCount());
                        System.out.println("Count of 'currentSplit': " + currentSplit.getCount());

                        if (currentSplit.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                            System.out.println("Has capability! yay!");
                            IFluidTankProperties tankProperties = currentSplit.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()[0];
                            if (tankProperties.getContents() != null) {
                                ItemStack copy = currentSplit.copy();
                                copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()[0].getContents().amount, true);
                                if (!GeneralUtils.canAddItemToSlot(this.inputHandler.getStackInSlot(2), copy, false, true)) {
                                    return;
                                }

                                if (tankProperties.getContents().getFluid().equals(ModFluids.STABILIZER) || tankProperties.getContents().getFluid().getName().equalsIgnoreCase(YMConfig.alternativeStabilizer)) {
                                    System.out.println(tankProperties.getContents().amount);

                                    if (tankProperties.getContents().amount > MAX_STABILIZER - getSTank().getFluidAmount()) { //given fluid is more than what fits in the S-Tank
                                        if(tankProperties.canDrainFluidType(new FluidStack(tankProperties.getContents().getFluid(), MAX_STABILIZER - getSTank().getFluidAmount()))) {
                                            getSTank().fill(currentSplit.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(MAX_STABILIZER - getSTank().getFluidAmount(), true), true);
                                        }
                                    } else { //given fluid fits perfectly in S-Tank
                                        getSTank().fill(currentSplit.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(tankProperties.getContents().amount, true), true);
                                    }
                                }
                            }
                        }
                        this.inputHandler.setStackInSlot(1, item.getCount() > 0 ? item : ItemStack.EMPTY);
                        this.inputHandler.insertItem(2, currentSplit, false);
                    }
                }
            }
            currentPartTick++;
        } else {
            currentPartTick++;
        }
    }

    private Object[] getNeighborTileEntity(BlockPos creatorPos) {
        HashMap<BlockPos, EnumFacing> foundPos = new HashMap<>();
        for(EnumFacing facing : EnumFacing.VALUES) {
            if(world.getTileEntity(creatorPos.offset(facing)) != null) {
                if (Objects.requireNonNull(world.getTileEntity(creatorPos.offset(facing))).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)) {
                    foundPos.put(creatorPos.offset(facing), facing);
                }
            }
        }

        // Prioritize Replicator
        for (Map.Entry<BlockPos, EnumFacing> entry : foundPos.entrySet()) {
            if (world.getTileEntity( entry.getKey()) instanceof TileReplicator) {
                if(Objects.requireNonNull(world.getTileEntity(entry.getKey())).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, entry.getValue()).fill(new FluidStack(ModFluids.UMATTER, 500), false) > 0) {
                    //Replicator can take fluid
                    return new Object[] {entry.getKey(), entry.getValue()}; // position, facing
                }
            }
        }

        // Replicator not found / can't take fluid, now trying other blocks
        for (Map.Entry<BlockPos, EnumFacing> entry : foundPos.entrySet()) {
            if(Objects.requireNonNull(world.getTileEntity(entry.getKey())).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, entry.getValue()).fill(new FluidStack(ModFluids.UMATTER, 500), false) > 0) {
                //Tile can take fluid
                return new Object[] {entry.getKey(), entry.getValue()}; // position, facing
            }
        }

        // found nothing
        return null;
    }
}
