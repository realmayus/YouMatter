package realmayus.youmatter.encoder;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.util.IGuiTile;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileEncoder extends TileEntity implements IGuiTile, ITickable {


    private List<ItemStack> queue = new ArrayList<>();

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
    ItemStackHandler inputHandler = new ItemStackHandler(5) {

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileEncoder.this.markDirty();
        }
    };


    // Calling this method signals incoming data from a neighboring scanner
    public void ignite(ItemStack itemStack) {
        if(itemStack != ItemStack.EMPTY && itemStack != null) {
            queue.add(itemStack);
        }
    }

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
        // only run on server
        if (!world.isRemote) {
            if(queue.size() > 0){
                ItemStack processIS = queue.get(queue.size() - 1);
                if(processIS != ItemStack.EMPTY) {
                    if(this.inputHandler.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                        if (progress < 100) {
                            if(getEnergy() >= 2048) {
                                NBTTagCompound nbt = this.inputHandler.getStackInSlot(1).getTagCompound();
                                if (nbt != null) {
                                    if (nbt.hasKey("stored_items")) {
                                        NBTTagList list = nbt.getTagList("stored_items", Constants.NBT.TAG_STRING);
                                        if (list.tagCount() < 8) {
                                            progress = progress + 1;
                                        }
                                    }
                                } else {
                                    progress = progress + 1; //doesn't have data stored yet
                                }
                            }
                        } else {
                            NBTTagCompound nbt = this.inputHandler.getStackInSlot(1).getTagCompound();
                            if (nbt != null) {
                                if(nbt.hasKey("stored_items")) {
                                    NBTTagList list = nbt.getTagList("stored_items", Constants.NBT.TAG_STRING);
                                    if(list.tagCount() < 8) {
                                        list.appendTag(new NBTTagString(processIS.getItem().getRegistryName() + "|" + processIS.getMetadata()));
                                        nbt.setTag("stored_items", list);
                                    }
                                } else {
                                    NBTTagList list = new NBTTagList();
                                    list.appendTag(new NBTTagString(processIS.getItem().getRegistryName() + "|" + processIS.getMetadata()));
                                    nbt.setTag("stored_items", list);
                                }
                            } else {
                                nbt = new NBTTagCompound();
                                NBTTagList list = new NBTTagList();
                                list.appendTag(new NBTTagString(processIS.getItem().getRegistryName() + "|" + processIS.getMetadata()));
                                nbt.setTag("stored_items", list);
                                this.inputHandler.getStackInSlot(1).setTagCompound(nbt);
                            }

                            queue.remove(processIS);
                            progress = 0;
                        }
                    }
                }
            }
        }
    }
}

