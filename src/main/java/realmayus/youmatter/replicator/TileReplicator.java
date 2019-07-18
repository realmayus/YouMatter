package realmayus.youmatter.replicator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.ModItems;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateReplicatorClient;
import realmayus.youmatter.util.IGuiTile;

import javax.annotation.Nonnull;

public class TileReplicator extends TileEntity implements IGuiTile, ITickable{


    public static final int MAX_UMATTER = 10000;

    private FluidTank tank = new FluidTank(MAX_UMATTER) {
        @Override
        protected void onContentsChanged() {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    public FluidTank getTank() {
        return tank;
    }


    @Override
    public Container createContainer(EntityPlayer player) {
        return new ContainerReplicator(player.inventory, this);
    }

    @Override
    public GuiContainer createGui(EntityPlayer player) {
        return new GuiReplicator(this, new ContainerReplicator(player.inventory, this));
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
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        }

        return super.getCapability(capability, facing);
    }

    //int has to be 0 as we don't want to receive energy
    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, 0);

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

    private CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler, outputHandler);


    private int currentPartTick = 0;
    @Override
    public void update() {
        if(currentPartTick == 5) {
            currentPartTick = 0;

            //only execute this code on the server
            if(!world.isRemote) {

                //TODO: URGENT!!!! Send this packet only to those who have the GUI opened!
                PacketHandler.INSTANCE.sendToAll(new PacketUpdateReplicatorClient(getTank().getFluidAmount(), getEnergy(), 10, this.getTank().writeToNBT(new NBTTagCompound())));
                if (!this.inputHandler.getStackInSlot(3).isEmpty()) {

                    if (this.inputHandler.getStackInSlot(3).isItemEqual(FluidUtil.getFilledBucket(new FluidStack(ModFluids.UMATTER, ModFluids.UMATTER.BUCKET_VOLUME)))) {
                        if (getTank().getFluidAmount() + 1000 < getTank().getCapacity()) {
                            getTank().fill(new FluidStack(ModFluids.UMATTER, 1000), true);
                            System.out.println("Current tank amount: " + getTank().getFluidAmount() + "mB");
                            this.inputHandler.setStackInSlot(3, ItemStack.EMPTY);
                            this.combinedHandler.insertItem(4, new ItemStack(Items.BUCKET, 1), false);

                        } else {
                            System.out.println("tank capacity exceeded.");
                        }
                    } else {
                        System.out.println("Definitely not a umatter bucket!");

                    }
                }
            }
        }
        currentPartTick++;
    }


    public int getClientProgress() {
        return clientProgress;
    }

    public void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }

    public int getClientFluidAmount() {
        return clientFluidAmount;
    }

    public void setClientFluidAmount(int clientFluidAmount) {
        this.clientFluidAmount = clientFluidAmount;
    }

    private int clientFluidAmount = -1;

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
        FluidTank newTank = new FluidTank(10000).readFromNBT(tank);
        getTank().setFluid(newTank.getFluid());
        getTank().setCapacity(newTank.getCapacity());
    }


    private int clientEnergy = -1;
    private int clientProgress = -1;



}
