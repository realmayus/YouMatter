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
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.encoder.BlockEncoder;
import realmayus.youmatter.encoder.TileEncoder;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

public class TileScanner extends TileEntity implements  ITickable{

    public TileScanner() {
    }

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
        setProgress(compound.getInteger("progress"));
        myEnergyStorage.setEnergy(compound.getInteger("energy"));
        inputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsIN"));
        outputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsOUT"));
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("progress", getProgress());
        compound.setInteger("energy", getEnergy());
        compound.setTag("itemsIN", inputHandler.serializeNBT());
        compound.setTag("itemsOUT", outputHandler.serializeNBT());
        return compound;
    }

    private int currentPartTick = 0;
    @Override
    public void update() {
        if(currentPartTick >= 2) {
            if (getNeighborEncoder(this.pos) != null) {
                hasEncoder = true;
                BlockPos encoderPos = getNeighborEncoder(this.pos);
                if(!inputHandler.getStackInSlot(1).isEmpty() && isItemAllowed(inputHandler.getStackInSlot(1))) {
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

    private boolean isItemAllowed(ItemStack itemStack) {
            //If list should act as a blacklist AND it contains the item, disallow scanning
        if (YMConfig.useAsBlacklist && Arrays.stream(YMConfig.itemList).anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(itemStack.getItem().getRegistryName()).toString()))) {
            return false;

            //If list should act as a whitelist AND it DOESN'T contain the item, disallow scanning
        } else if (!(YMConfig.useAsBlacklist) && Arrays.stream(YMConfig.itemList).noneMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(itemStack.getItem().getRegistryName()).toString()))) {
            return false;

            //Else:
        } else {
            return true;
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
