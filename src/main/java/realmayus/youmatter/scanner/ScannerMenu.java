package realmayus.youmatter.scanner;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import realmayus.youmatter.ModContent;

public class ScannerMenu extends AbstractContainerMenu {

    public ScannerBlockEntity scanner;
    private IItemHandler playerInventory;


    public ScannerMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContent.SCANNER_MENU.get(), windowId);
        scanner = level.getBlockEntity(pos) instanceof ScannerBlockEntity scanner ? scanner : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = scanner.getLevel();
        BlockPos pos = scanner.getBlockPos();

        return !level.getBlockState(pos).is(ModContent.SCANNER_BLOCK.get()) ? false : player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }


    private void addPlayerSlots(IItemHandler itemHandler) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = col * 18 + 8;
                int y = row * 18 + 85;
                addSlot(new SlotItemHandler(itemHandler, col + row * 9 + 9, x, y));
            }
        }
        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 143;
            addSlot(new SlotItemHandler(itemHandler, row, x, y));
        }
    }

    private void addCustomSlots() {
        scanner.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> addSlot(new SlotItemHandler(h, 1, 80, 37)));
    }


    /**
     * This is actually needed in order to achieve shift click functionality in the GUI. If this method isn't overridden, the game crashes.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index == 36) {
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 36, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }
}
