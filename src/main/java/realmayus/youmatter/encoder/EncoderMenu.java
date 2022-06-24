package realmayus.youmatter.encoder;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.items.ThumbdriveItem;

public class EncoderMenu extends AbstractContainerMenu {


    public EncoderBlockEntity encoder;
    private IItemHandler playerInventory;

    public EncoderMenu(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(ObjectHolders.ENCODER_MENU_TYPE, windowId);
        encoder = world.getBlockEntity(pos) instanceof EncoderBlockEntity encoder ? encoder : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = encoder.getLevel();
        BlockPos pos = encoder.getBlockPos();

        return !level.getBlockState(pos).is(ObjectHolders.ENCODER_BLOCK) ? false : player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
        encoder.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 1, 90, 38)));
    }

    /**
     * This is actually needed in order to achieve shift click functionality in the GUI. If this method isn't overridden, the game crashes.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem() && slot.getItem().getItem() instanceof ThumbdriveItem) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index == 36) { //originating slot is custom slot
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else if (!this.moveItemStackTo(slotStack, 36, 37, false)) { //move from inv to custom slot
                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
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
