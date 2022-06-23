package realmayus.youmatter.creator;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateCreatorClient;



public class CreatorMenu extends AbstractContainerMenu implements ICreatorStateContainer {

    public CreatorBlockEntity creator;
    private IItemHandler playerInventory;


    public CreatorMenu(int windowId, Level level, BlockPos pos, Inventory playerInventory, Player player) {
        super(ObjectHolders.CREATOR_CONTAINER, windowId);
        creator = level.getBlockEntity(pos) instanceof CreatorBlockEntity creator ? creator : null;
        this.playerInventory = new InvWrapper(playerInventory);

        addPlayerSlots(this.playerInventory);
        addCustomSlots();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        for(ContainerListener p : this.containerListeners) {
            if (p instanceof ServerPlayer serverPlayer) {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketUpdateCreatorClient(creator.getEnergy(), 0, creator.getUTank().getFluid(), creator.getSTank().getFluid(), creator.isActivated()));
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
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
        creator.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 1, 52, 20));
            addSlot(new SlotItemHandler(h, 2, 52, 62));
            addSlot(new SlotItemHandler(h, 3, 110, 20));
            addSlot(new SlotItemHandler(h, 4, 110, 62));
        });
    }

    /**
     * This is actually needed in order to achieve shift click functionality in the Controller GUI. If this method isn't overridden, the game crashes.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index >= 36 && index <= 39) { //originating slot is custom slot
                if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else {
                if(slotStack.getItem() instanceof BucketItem bucket) {
                    if (bucket.getFluid().equals(ModFluids.STABILIZER.get()) || YMConfig.CONFIG.alternativeStabilizer.get().equalsIgnoreCase(bucket.getFluid().getRegistryName().getPath())) {
                        if(!this.moveItemStackTo(slotStack, 36, 37, false)) {
                            return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                        }
                    } else if(bucket == Items.BUCKET) {
                        if(!this.moveItemStackTo(slotStack, 38, 39, false)) {
                            return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                        }
                    }
                } else if(slotStack.getItem().equals(Items.BUCKET)) {
                    if(!this.moveItemStackTo(slotStack, 38, 39, false)) {
                        return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                    }

                } else if(slotStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    return slotStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(h -> {
                        if (h.getFluidInTank(0).isEmpty() || h.getFluidInTank(0).getFluid().isSame(ModFluids.UMATTER.get())) {
                            if(!this.moveItemStackTo(slotStack, 38, 39, false)) {
                                return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                            }
                        } else if (h.getFluidInTank(0).getFluid().isSame(ModFluids.STABILIZER.get()) || YMConfig.CONFIG.alternativeStabilizer.get().equalsIgnoreCase(h.getFluidInTank(0).getFluid().getRegistryName().getPath())) {
                            if(!this.moveItemStackTo(slotStack, 36, 37, false)) {
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
    public void sync(int energy, int progress, FluidStack uTank, FluidStack sTank, boolean isActivated) {
        creator.setEnergy(energy);
        creator.getUTank().setFluid(uTank);
        creator.getSTank().setFluid(sTank);
        creator.setActivated(isActivated);
    }
}
