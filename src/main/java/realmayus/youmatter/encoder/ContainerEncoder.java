//package realmayus.youmatter.encoder;
//
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.inventory.Container;
//import net.minecraft.inventory.IContainerListener;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.items.CapabilityItemHandler;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.SlotItemHandler;
//import realmayus.youmatter.items.ThumbdriveItem;
//import realmayus.youmatter.network.PacketHandler;
//import realmayus.youmatter.network.PacketUpdateEncoderClient;
//
//public class ContainerEncoder extends Container implements IEncoderStateContainer {
//    public TileEncoder te;
//
//
//    public ContainerEncoder(IInventory playerInventory, TileEncoder te) {
//        this.te = te;
//        addPlayerSlots(playerInventory);
//        addCustomSlots();
//    }
//
//    @Override
//    public void detectAndSendChanges() {
//        super.detectAndSendChanges();
//        for(IContainerListener p : listeners) {
//            if(p instanceof EntityPlayerMP) {
//                PacketHandler.INSTANCE.sendTo(new PacketUpdateEncoderClient(te.getEnergy(), te.getProgress()), (EntityPlayerMP)p);
//            }
//        }
//    }
//
//    @Override
//    public boolean canInteractWith(EntityPlayer playerIn) {
//        return te.canInteractWith(playerIn);
//    }
//
//    private void addPlayerSlots(IInventory playerInventory) {
//        // Slots for the main inventory
//        for (int row = 0; row < 3; ++row) {
//            for (int col = 0; col < 9; ++col) {
//                int x = col * 18 + 8;
//                int y = row * 18 + 86;
//                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
//            }
//        }
//        // Slots for the hotbar
//        for (int row = 0; row < 9; ++row) {
//            int x = 8 + row * 18;
//            int y = 144;
//            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
//        }
//    }
//
//    private void addCustomSlots() {
//        IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
//
//        addSlotToContainer(new SlotItemHandler(itemHandler, 1, 90, 39));
//    }
//
//    /**
//     * This is actually needed in order to achieve shift click functionality in the Controller GUI. If this method isn't overridden, the game crashes.
//     */
//    @Override
//    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = this.inventorySlots.get(index);
//
//        if (slot != null && slot.getHasStack() && slot.getStack().getItem() instanceof ThumbdriveItem) {
//            ItemStack itemstack1 = slot.getStack();
//            itemstack = itemstack1.copy();
//
//            if (index == 36) { //originating slot is custom slot
//                if (!this.mergeItemStack(itemstack1, 0, 36, true)) {
//                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
//                }
//            } else if (!this.mergeItemStack(itemstack1, 36, 37, false)) { //move from inv to custom slot
//                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
//            }
//
//            if (itemstack1.isEmpty()) {
//                slot.putStack(ItemStack.EMPTY);
//            } else {
//                slot.onSlotChanged();
//            }
//        }
//
//        return itemstack;
//    }
//
//
//
//    @Override
//    public void sync(int energy, int progress) {
//        te.setClientEnergy(energy);
//        te.setClientProgress(progress);
//    }
//}
