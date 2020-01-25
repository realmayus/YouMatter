package realmayus.youmatter.creator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketUpdateCreatorClient;


public class ContainerCreator extends Container implements ICreatorStateContainer {
    public TileCreator te;

    /**
     * 0 = Ignore Redstone
     * 1 = Active on Redstone
     * 2 = Not active on Redstone
     */
    public int redstoneBehaviour = 0;
    public boolean isEnabled = true;

    public ContainerCreator(IInventory playerInventory, TileCreator te) {
        this.te = te;
        addPlayerSlots(playerInventory);
        addCustomSlots();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for(IContainerListener p : listeners) {
            if(p instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PacketUpdateCreatorClient(te.getUTank().getFluidAmount(), te.getSTank().getFluidAmount(), te.getEnergy(), 10, te.getUTank().writeToNBT(new NBTTagCompound()), te.getSTank().writeToNBT(new NBTTagCompound())), (EntityPlayerMP)p);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.canInteractWith(playerIn);
    }

    private void addPlayerSlots(IInventory playerInventory) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = col * 18 + 8;
                int y = row * 18 + 86;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 144;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));

        }
    }

    private void addCustomSlots() {
        IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        // stabilizer bucket input slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 1, 52, 21));
        // stabilizer bucket output slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 2, 52, 63));

        // u-matte++++r bucket input slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 3, 110, 21));
        // u-matter bucket output slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 4, 110, 63));
    }

    @Override
    public void sync(int uFluidAmount, int sFluidAmount, int energy, int progress, NBTTagCompound uTank, NBTTagCompound sTank) {
        te.setClientUFluidAmount(uFluidAmount);
        te.setClientSFluidAmount(sFluidAmount);
        te.setClientEnergy(energy);
        te.setClientProgress(progress);
        te.setClientUTank(uTank);
        te.setClientSTank(sTank);
    }



}
