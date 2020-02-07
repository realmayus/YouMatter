package realmayus.youmatter.scanner;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import realmayus.youmatter.encoder.BlockEncoder;
import realmayus.youmatter.encoder.TileEncoder;
import realmayus.youmatter.util.IGuiTile;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;

public class TileScanner extends TileEntity implements IGuiTile, ITickable{

    public boolean hasEncoder = false;

    public boolean getHasEncoder() {
        return hasEncoder;
    }

    public void setHasEncoder(boolean hasEncoder) {
        this.hasEncoder = hasEncoder;
    }

    public boolean getHasEncoderClient() {
        return hasEncoderClient;
    }

    public void setHasEncoderClient(boolean hasEncoderClient) {
        this.hasEncoderClient = hasEncoderClient;
    }

    public boolean hasEncoderClient = false;

    @Override
    public Container createContainer(EntityPlayer player) {
        return new ContainerScanner(player.inventory, this);
    }

    @Override
    public GuiContainer createGui(EntityPlayer player) {
        return new GuiScanner(this, new ContainerScanner(player.inventory, this));
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
            TileScanner.this.markDirty();
        }
    };



    /**
     * Handler for the Output Slots
     */
    private ItemStackHandler outputHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            TileScanner.this.markDirty();
        }
    };

    private CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler, outputHandler);


    private int clientEnergy = -1;
    private int clientProgress = -1;

    private int progress = 0;


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getClientProgress() {
        return clientProgress;
    }

    public void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }

    public int getClientEnergy() {
        return clientEnergy;
    }

    public void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }






    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, Integer.MAX_VALUE);

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        return compound;
    }

    private int currentPartTick = 0;
    @Override
    public void update() {

        if(currentPartTick >= 2) {
            if (getNeighborEncoder(this.pos) != null) {
                hasEncoder = true;
                BlockPos encoderPos = getNeighborEncoder(this.pos);
                if(!inputHandler.getStackInSlot(1).isItemEqual(ItemStack.EMPTY)) {
                    if(getEnergy() > 2048) {
                        if (getProgress() < 100) {
                            setProgress(getProgress() + 1);
                            myEnergyStorage.consumePower(2048);
                        } else if (encoderPos != null) {
                            // Notifying the neighboring encoder of this scanner having finished its operation
                            ((TileEncoder)world.getTileEntity(encoderPos)).ignite(this.inputHandler.getStackInSlot(1)); //don't worry, this is already checked by getNeighborEncoder() c:
                            inputHandler.setStackInSlot(1, ItemStack.EMPTY);
                            setProgress(0);
                        }
                    }
                } else {
                    setProgress(0); // if item was suddenly removed, reset progress to 0
                }
            } else {
                hasEncoder = false;
            }
            currentPartTick = 0;
        } else {
            currentPartTick++;
        }
    }

    private BlockPos getNeighborEncoder(BlockPos scannerPos) {
        for(EnumFacing facing : EnumFacing.VALUES) {
            if(world.getBlockState(scannerPos.offset(facing)).getBlock() instanceof BlockEncoder) {
                if(world.getTileEntity(scannerPos.offset(facing)) != null) {
                    if(world.getTileEntity(scannerPos.offset(facing)) instanceof TileEncoder) {
                        return scannerPos.offset(facing);
                    }
                }
            }
        }

        return null;
    }

}
