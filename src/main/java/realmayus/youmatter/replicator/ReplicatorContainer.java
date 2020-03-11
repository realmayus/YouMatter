package realmayus.youmatter.replicator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateReplicatorClient;
import realmayus.youmatter.util.DisplaySlot;

public class ReplicatorContainer extends Container implements IReplicatorStateContainer {

    public ReplicatorTile te;
    private PlayerEntity playerEntity;
    private IItemHandler playerInventory;

    public ReplicatorContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ObjectHolders.REPLICATOR_CONTAINER, windowId);
        te = world.getTileEntity(pos) instanceof ReplicatorTile ? (ReplicatorTile) world.getTileEntity(pos) : null;
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
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) p), new PacketUpdateReplicatorClient(te.getEnergy(), te.getProgress(), te.isActive(), te.isCurrentMode(), te.getTank().getFluid()));
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
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
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
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index >= 36 && index <= 40) { //originating slot is custom slot
                if (!this.mergeItemStack(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY; // Inventory is full, can't transfer item!
                }
            } else {
                if (itemstack1.getItem() instanceof ThumbdriveItem) {
                    if(!this.mergeItemStack(itemstack1, 36, 37, false)) {
                        return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                    }
                } else if(itemstack1.getItem() instanceof BucketItem) {
                    BucketItem bucket = (BucketItem) itemstack1.getItem();
                    if(bucket.getFluid().getFluid().equals(ModFluids.UMATTER.get())) {
                        if(!this.mergeItemStack(itemstack1, 39, 40, false)) {
                            return ItemStack.EMPTY; // custom slot is full, can't transfer item!
                        }
                    }
                } else if(itemstack1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                    return itemstack1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(h -> {
                        if (h.getFluidInTank(0).getFluid().isEquivalentTo(ModFluids.UMATTER.get())) {
                            if (!this.mergeItemStack(itemstack1, 39, 40, false)) {
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

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void sync(int energy, int progress, FluidStack tank, boolean isActivated, boolean mode) {
        te.setClientEnergy(energy);
        te.setClientProgress(progress);
        te.getTank().setFluid(tank);
        te.setCurrentClientMode(mode);
        te.setActiveClient(isActivated);
    }
}
