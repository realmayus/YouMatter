package realmayus.youmatter.encoder;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.PacketDistributor;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateEncoderClient;

public class EncoderContainer extends AbstractContainerMenu implements IEncoderStateContainer {


    public EncoderTile te;
    private IItemHandler playerInventory;

    public EncoderContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
        super(ObjectHolders.ENCODER_CONTAINER, windowId);
        te = world.getBlockEntity(pos) instanceof EncoderTile encoder ? encoder : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for(ContainerListener p : this.containerListeners) {
            if(p != null) {
                if (p instanceof ServerPlayer serverPlayer) {
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketUpdateEncoderClient(te.getEnergy(), te.getProgress()));
                }
            }
        }
    }

    @Override
    public boolean stillValid(Player playerIn) {
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
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 1, 90, 38)));
    }

    /**
     * This is actually needed in order to achieve shift click functionality in the GUI. If this method isn't overridden, the game crashes.
     */
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem() && slot.getItem().getItem() instanceof ThumbdriveItem) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 36) { //originating slot is custom slot
                if (!this.moveItemStackTo(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else if (!this.moveItemStackTo(itemstack1, 36, 37, false)) { //move from inv to custom slot
                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void sync(int energy, int progress) {
        te.setEnergy(energy);
        te.setProgress(progress);
    }
}
