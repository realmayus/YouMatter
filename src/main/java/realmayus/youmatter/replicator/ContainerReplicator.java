package realmayus.youmatter.replicator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateReplicatorClient;
import realmayus.youmatter.util.DisplaySlot;

public class ContainerReplicator extends Container implements IReplicatorStateContainer {
    public TileReplicator te;

    public ContainerReplicator(IInventory playerInventory, TileReplicator te) {
        this.te = te;
        addPlayerSlots(playerInventory);
        addCustomSlots();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for(IContainerListener p : listeners) {
            if(p instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PacketUpdateReplicatorClient(te.getTank().getFluidAmount(), te.getEnergy(), te.getProgress(), te.getTank().writeToNBT(new NBTTagCompound()), te.isActive(), te.isCurrentMode()), (EntityPlayerMP)p);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.canInteractWith(playerIn);
    }

    private void addPlayerSlots(IInventory playerInventory) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = col * 18 + 8;
                int y = row * 18 + 86;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 144;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));

        }
    }

    private void addCustomSlots() {
        IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // Flash drive
        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 150, 61));
        // Output slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 1, 89, 61));
        // Item Display slot
        addSlotToContainer(new DisplaySlot(itemHandler, 2, 89, 18));
        // bucket input slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 3, 47, 19));
        // bucket output slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 4, 47, 61));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index >= 36 && index <= 40) { //originating slot is custom slot
                if (!this.mergeItemStack(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else {
                if (itemstack1.getItem() instanceof ThumbdriveItem) {
                    if(!this.mergeItemStack(itemstack1, 36, 37, false)) {
                        return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                    }
                } else if(itemstack1.getItem() instanceof UniversalBucket) {
                    UniversalBucket bucket = (UniversalBucket) itemstack1.getItem();
                    if(bucket.getFluid(itemstack1) != null) {
                        if (bucket.getFluid(itemstack1).getFluid().equals(ModFluids.UMATTER)) {
                            if(!this.mergeItemStack(itemstack1, 39, 40, false)) {
                                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                            }
                        }
                    }
                }
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void sync(int fluidAmount, int energy, int progress, NBTTagCompound tank, boolean isActivated, boolean mode) {
        te.setClientFluidAmount(fluidAmount);
        te.setClientEnergy(energy);
        te.setClientProgress(progress);
        te.setClientTank(tank);
        te.setCurrentClientMode(mode);
        te.setActiveClient(isActivated);
    }

}
