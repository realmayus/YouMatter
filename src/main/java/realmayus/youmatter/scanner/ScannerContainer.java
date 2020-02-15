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

    public ScannerTile te;
    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;


    public ScannerContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ObjectHolders.SCANNER_CONTAINER, windowId);
        te = world.getTileEntity(pos) instanceof ScannerTile ? (ScannerTile) world.getTileEntity(pos) : null;
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for(IContainerListener p : this.listeners) {
            if(p != null) {
                if (p instanceof ServerPlayerEntity) {
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) p), new PacketUpdateScannerClient(te.getEnergy(), te.getProgress(), te.getHasEncoder()));
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }


    private void addPlayerSlots(IItemHandler iItemHandler) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = col * 18 + 8;
                int y = row * 18 + 85;
                addSlot(new SlotItemHandler(iItemHandler, col + row * 9 + 9, x, y));
            }
        }
        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 143;
            addSlot(new SlotItemHandler(iItemHandler, row, x, y));
        }
    }

    private void addCustomSlots() {
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 1, 80, 38)));
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
