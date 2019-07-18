package realmayus.youmatter.replicator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import realmayus.youmatter.util.IReplicatorStateContainer;

public class ContainerReplicator extends Container implements IReplicatorStateContainer {
    public TileReplicator te;

    /**
     * 0 = Ignore Redstone
     * 1 = Active on Redstone
     * 2 = Not active on Redstone
     */
    public int redstoneBehaviour = 0;
    public boolean isEnabled = true;

    public ContainerReplicator(IInventory playerInventory, TileReplicator te) {
        this.te = te;
        addPlayerSlots(playerInventory);
        addCustomSlots();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
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
                int y = row * 18 + 84;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 58 + 84;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));

        }
    }

    private void addCustomSlots() {
        IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // Flash drive
        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 149, 60));
        // Output slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 1, 88, 60));
        // Item to replicate slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 2, 88, 18));
        // bucket input slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 3, 46, 18));
        // bucket output slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 4, 46, 60));
    }

    @Override
    public void sync(int fluidAmount, int energy, int progress, NBTTagCompound tank) {
        te.setClientFluidAmount(fluidAmount);
        te.setClientEnergy(energy);
        te.setClientProgress(progress);
        te.setClientTank(tank);
    }
    
}
