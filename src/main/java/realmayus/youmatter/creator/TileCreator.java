package realmayus.youmatter.creator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateCreatorClient;
import realmayus.youmatter.util.IGuiTile;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;

public class TileCreator extends TileEntity implements IGuiTile, ITickable{

    @Override
    public Container createContainer(EntityPlayer player) {
        return new ContainerCreator(player.inventory, this);
    }

    @Override
    public GuiContainer createGui(EntityPlayer player) {
        return new GuiCreator(this, new ContainerCreator(player.inventory, this));
    }

    public static final int MAX_UMATTER = 10000;
    public static final int MAX_STABILIZER = 20000;

    private FluidTank uTank = new FluidTank(MAX_UMATTER + 1000) {
        @Override
        protected void onContentsChanged() {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    private FluidTank sTank = new FluidTank(MAX_STABILIZER + 1000) {
        @Override
        protected void onContentsChanged() {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    public FluidTank getUTank() {
        return uTank;

    }

    public FluidTank getSTank() {
        return sTank;
    }

    /**
     * If we are too far away from this tile entity you cannot use it
     */
    public boolean canInteractWith(EntityPlayer playerIn) {
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

        return super.getCapability(capability, facing);
    }

    //int has to be 0 as we don't want to receive energy
    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, 2000);

    /**
     * Handler for the Input Slots
     */
    public ItemStackHandler inputHandler = new ItemStackHandler(5) {

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

    public void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }



    public void setClientUFluidAmount(int clientUFluidAmount) {
        this.clientUFluidAmount = clientUFluidAmount;
    }
    public void setClientSFluidAmount(int clientSFluidAmount) {
        this.clientSFluidAmount = clientSFluidAmount;
    }

    private int clientUFluidAmount = -1;
    private int clientSFluidAmount = -1;

    public int getClientEnergy() {
        return clientEnergy;
    }

    public void setClientEnergy(int clientEnergy) {
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


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagCompound tagUTank = compound.getCompoundTag("uTank");
        NBTTagCompound tagSTank = compound.getCompoundTag("sTank");
        uTank.readFromNBT(tagUTank);
        sTank.readFromNBT(tagSTank);
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
        return compound;
    }

    public void setClientUTank(NBTTagCompound tank) {
        FluidTank newTank = new FluidTank(10000).readFromNBT(tank);
        getUTank().setFluid(newTank.getFluid());
        getUTank().setCapacity(newTank.getCapacity());
    }

    public void setClientSTank(NBTTagCompound tank) {
        FluidTank newTank = new FluidTank(10000).readFromNBT(tank);
        getSTank().setFluid(newTank.getFluid());
        getSTank().setCapacity(newTank.getCapacity());
    }

    private int currentPartTick = 0;
    @Override
    public void update() {
        if(currentPartTick == 5) {
            if(!world.isRemote) {
                //TODO: URGENT!!!! Send this packet only to those who have the GUI opened!
                PacketHandler.INSTANCE.sendToAll(new PacketUpdateCreatorClient(getUTank().getFluidAmount(), getSTank().getFluidAmount(), getEnergy(), 10, this.getUTank().writeToNBT(new NBTTagCompound()), this.getSTank().writeToNBT(new NBTTagCompound())));
                if (!this.inputHandler.getStackInSlot(3).isEmpty()) {
                    if(this.inputHandler.getStackInSlot(3).getItem() instanceof UniversalBucket) {
                        UniversalBucket bucket = (UniversalBucket) this.inputHandler.getStackInSlot(3).getItem();
                        if(bucket.getFluid(this.inputHandler.getStackInSlot(3)) != null) {
                            if (bucket.getFluid(this.inputHandler.getStackInSlot(3)).getFluid().equals(ModFluids.UMATTER)) {
                                if (getUTank().getFluidAmount() + 1000 < getUTank().getCapacity()) {
                                    getUTank().fill(new FluidStack(ModFluids.UMATTER, 1000), true);
                                    this.inputHandler.setStackInSlot(3, ItemStack.EMPTY);
                                    this.combinedHandler.insertItem(4, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        }
                    }
                }
                if (!this.inputHandler.getStackInSlot(1).isEmpty()) {
                    if(this.inputHandler.getStackInSlot(1).getItem() instanceof UniversalBucket) {
                        UniversalBucket bucket = (UniversalBucket) this.inputHandler.getStackInSlot(1).getItem();
                        if(bucket.getFluid(this.inputHandler.getStackInSlot(1)) != null) {
                            if (bucket.getFluid(this.inputHandler.getStackInSlot(1)).getFluid().equals(ModFluids.STABILIZER)) {
                                if (getSTank().getFluidAmount() + 1000 < getUTank().getCapacity()) {
                                    getSTank().fill(new FluidStack(ModFluids.STABILIZER, 1000), true);
                                    this.inputHandler.setStackInSlot(1, ItemStack.EMPTY);
                                    this.combinedHandler.insertItem(2, new ItemStack(Items.BUCKET, 1), false);
                                }
                            }
                        }
                    }
                }
            }
            currentPartTick = 0;
        } else {
            currentPartTick++;
        }
    }
}
