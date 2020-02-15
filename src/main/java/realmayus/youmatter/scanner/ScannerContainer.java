package realmayus.youmatter.scanner;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateScannerClient;

public class ScannerContainer extends Container implements IScannerStateContainer {

    private ScannerTile te;
    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;


    public ScannerContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ObjectHolders.SCANNER_CONTAINER_TYPE, windowId);
        te = world.getTileEntity(pos) instanceof ScannerTile ? (ScannerTile) world.getTileEntity(pos) : null;
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(10, 70);
        addCustomSlots();

    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for(IContainerListener p : this.listeners) { //todo
            if(p instanceof PlayerEntity) {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) p), new PacketUpdateScannerClient(te.getEnergy(), te.getProgress(), te.getHasEncoder()));
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    private void addPlayerSlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    private void addCustomSlots() {
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 1, 80, 38)));
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private void addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    /**
     * This is actually needed in order to achieve shift click functionality in the Controller GUI. If this method isn't overridden, the game crashes.
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 36) {
                if (!this.mergeItemStack(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 36, 37, false)) {
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
    public void sync(int energy, int progress, boolean hasEncoder) {
        te.setClientEnergy(energy);
        te.setClientProgress(progress);
        te.setHasEncoderClient(hasEncoder);
    }
}
