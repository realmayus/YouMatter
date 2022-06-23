package realmayus.youmatter.replicator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.PacketDistributor;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateReplicatorClient;
import realmayus.youmatter.util.DisplaySlot;

public class ReplicatorMenu extends AbstractContainerMenu implements IReplicatorStateContainer {

    public ReplicatorBlockEntity replicator;
    private IItemHandler playerInventory;

    public ReplicatorMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player player) {
        super(ObjectHolders.REPLICATOR_CONTAINER, windowId);
        replicator = level.getBlockEntity(pos) instanceof ReplicatorBlockEntity replicator ? replicator : null;
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
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketUpdateReplicatorClient(replicator.getEnergy(), replicator.getProgress(), replicator.isActive(), replicator.isCurrentMode(), replicator.getTank().getFluid()));
                }
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = replicator.getLevel();
        BlockPos pos = replicator.getBlockPos();

        return !level.getBlockState(pos).is(ObjectHolders.REPLICATOR_BLOCK) ? false : player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
        replicator.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
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
                    if(bucket.getFluid().equals(ModFluids.UMATTER.get())) {
                        if(!this.moveItemStackTo(slotStack, 39, 40, false)) {
                            return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                        }
                    }
                } else if(slotStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    return slotStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(h -> {
                        if (h.getFluidInTank(0).getFluid().isSame(ModFluids.UMATTER.get())) {
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

    @Override
    public void sync(int energy, int progress, FluidStack tank, boolean isActivated, boolean mode) {
        replicator.setEnergy(energy);
        replicator.setProgress(progress);
        replicator.getTank().setFluid(tank);
        replicator.setCurrentMode(mode);
        replicator.setActive(isActivated);
    }
}
