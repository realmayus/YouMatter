package realmayus.youmatter.replicator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import realmayus.youmatter.ModContent;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.util.DisplaySlot;

public class ReplicatorMenu extends AbstractContainerMenu {

    public ReplicatorBlockEntity replicator;
    private IItemHandler playerInventory;

    public ReplicatorMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContent.REPLICATOR_MENU.get(), windowId);
        replicator = level.getBlockEntity(pos) instanceof ReplicatorBlockEntity replicator ? replicator : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = replicator.getLevel();
        BlockPos pos = replicator.getBlockPos();

        return !level.getBlockState(pos).is(ModContent.REPLICATOR_BLOCK.get()) ? false : player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
        replicator.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            // Flash drive
            addSlot(new SlotItemHandler(h, 0, 150, 60));
            // Output slot
            addSlot(new SlotItemHandler(h, 1, 89, 60));
            // Item Display slot
            addSlot(new DisplaySlot(h, 2, 89, 17));
            // bucket input slot
            addSlot(new SlotItemHandler(h, 3, 47, 18));
            // bucket output slot
            addSlot(new SlotItemHandler(h, 4, 47, 60));
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index >= 36 && index <= 40) { //originating slot is custom slot
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else {
                if (slotStack.getItem() instanceof ThumbdriveItem) {
                    if(!this.moveItemStackTo(slotStack, 36, 37, false)) {
                        return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                    }
                } else if(slotStack.getItem() instanceof BucketItem bucket) {
                    if(bucket.getFluid().equals(ModContent.UMATTER.get())) {
                        if(!this.moveItemStackTo(slotStack, 39, 40, false)) {
                            return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                        }
                    }
                } else if(slotStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    return slotStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(h -> {
                        if (h.getFluidInTank(0).getFluid().isSame(ModContent.UMATTER.get())) {
                            if(!this.moveItemStackTo(slotStack, 39, 40, false)) {
                                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                            }
                        } else {
                            return ItemStack.EMPTY;
                        }
                        return ItemStack.EMPTY;
                    }).orElse(ItemStack.EMPTY);
                }
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
