package realmayus.youmatter.encoder;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class EncoderTile extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

    private List<ItemStack> queue = new ArrayList<>();

    public EncoderTile() {
        super(ObjectHolders.ENCODER_TILE);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> inventory).cast();
        }

        if(cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> myEnergyStorage).cast();

        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            EncoderTile.this.markDirty();
        }
    };


    // Calling this method signals incoming data from a neighboring scanner
    public void ignite(ItemStack itemStack) {
        if(itemStack != ItemStack.EMPTY && itemStack != null) {
            queue.add(itemStack);
        }
    }

    private int clientEnergy = -1;
    private int clientProgress = -1;

    private int progress = 0;


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    int getClientProgress() {
        return clientProgress;
    }

    void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }

    int getClientEnergy() {
        return clientEnergy;
    }

    void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, Integer.MAX_VALUE);

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        setProgress(compound.getInt("progress"));
        myEnergyStorage.setEnergy(compound.getInt("energy"));
        if(compound.contains("inventory")) {
            inventory.deserializeNBT((CompoundNBT) compound.get("inventory"));
        }
        if(compound.contains("queue")) {
            if (compound.get("queue") instanceof ListNBT) {
                List<ItemStack> queueBuilder = new ArrayList<>();
                for(INBT base: compound.getList("queue", Constants.NBT.TAG_COMPOUND)) {
                    if (base instanceof CompoundNBT) {
                        CompoundNBT nbtTagCompound = (CompoundNBT) base;
                        if(!ItemStack.read(nbtTagCompound).isEmpty()) {
                            queueBuilder.add(ItemStack.read(nbtTagCompound));
                        }
                    }
                }
                queue = queueBuilder;
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT());
        }
        ListNBT tempCompoundList = new ListNBT();
        for (ItemStack is : queue) {
            if (!is.isEmpty()) {
                tempCompoundList.add(is.write(new CompoundNBT()));
            }
        }
        compound.put("queue", tempCompoundList);
        return compound;
    }
    @Override
    public void remove() {
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i))));
    }

    @Override
    public void tick() {
        // only run on server
        if (!world.isRemote) {
            if(queue.size() > 0){
                ItemStack processIS = queue.get(queue.size() - 1);
                if(processIS != ItemStack.EMPTY) {
                    if(this.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                        if (progress < 100) {
                            if(getEnergy() >= YMConfig.CONFIG.energyEncoder.get()) {
                                CompoundNBT nbt = this.inventory.getStackInSlot(1).getTag();
                                if (nbt != null) {
                                    if (nbt.contains("stored_items")) {
                                        ListNBT list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
                                        if (list.size() < 8) {
                                            progress = progress + 1;
                                            myEnergyStorage.consumePower(YMConfig.CONFIG.energyEncoder.get());
                                        }
                                    }
                                } else {
                                    progress = progress + 1; //doesn't have data stored yet
                                    myEnergyStorage.consumePower(YMConfig.CONFIG.energyEncoder.get());
                                }
                            }
                        } else {
                            CompoundNBT nbt = this.inventory.getStackInSlot(1).getTag();
                            if (nbt != null) {
                                if(nbt.contains("stored_items")) {
                                    ListNBT list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
                                    if(list.size() < 8) {
                                        list.add(StringNBT.valueOf(processIS.getItem().getRegistryName() + ""));
                                        nbt.put("stored_items", list);
                                    }
                                } else {
                                    ListNBT list = new ListNBT();
                                    list.add(StringNBT.valueOf(processIS.getItem().getRegistryName() + ""));
                                    nbt.put("stored_items", list);
                                }
                            } else {
                                nbt = new CompoundNBT();
                                ListNBT list = new ListNBT();
                                list.add(StringNBT.valueOf(processIS.getItem().getRegistryName() + ""));
                                nbt.put("stored_items", list);
                                this.inventory.getStackInSlot(1).setTag(nbt);
                            }

                            queue.remove(processIS);
                            progress = 0;
                        }
                    }
                }
            }
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ObjectHolders.ENCODER_BLOCK.getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new EncoderContainer(windowID, world, pos, playerInventory, playerEntity);
    }
}

