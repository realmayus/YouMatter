package realmayus.youmatter.encoder;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import realmayus.youmatter.util.IGuiTile;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;

public class TileEncoder extends TileEntity implements IGuiTile, ITickable {
    @Override
    public Container createContainer(EntityPlayer player) {
        return new ContainerEncoder(player.inventory, this);
    }

    @Override
    public GuiContainer createGui(EntityPlayer player) {
        return new GuiEncoder(this, new ContainerEncoder(player.inventory, this));
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
            TileEncoder.this.markDirty();
        }
    };



    /**
     * Handler for the Output Slots
     */
    private ItemStackHandler outputHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            TileEncoder.this.markDirty();
        }
    };

    private CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler, outputHandler);


    private int clientEnergy = -1;
    private int clientProgress = -1;

    private int progress = 40;


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


    @Override
    public void update() {

    }
}
