package realmayus.youmatter.encoder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

public class EncoderTile extends BlockEntity implements MenuProvider, TickableBlockEntity {

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
            EncoderTile.this.setChanged();
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
    public void load(BlockState state, CompoundTag compound) {
        super.load(state, compound);
        setProgress(compound.getInt("progress"));
        myEnergyStorage.setEnergy(compound.getInt("energy"));
        if(compound.contains("inventory")) {
            inventory.deserializeNBT((CompoundTag) compound.get("inventory"));
        }
        if(compound.contains("queue")) {
            if (compound.get("queue") instanceof ListTag) {
                List<ItemStack> queueBuilder = new ArrayList<>();
                for(Tag base: compound.getList("queue", Constants.NBT.TAG_COMPOUND)) {
                    if (base instanceof CompoundTag) {
                        CompoundTag nbtTagCompound = (CompoundTag) base;
                        if(!ItemStack.of(nbtTagCompound).isEmpty()) {
                            queueBuilder.add(ItemStack.of(nbtTagCompound));
                        }
                    }
                }
                queue = queueBuilder;
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT());
        }
        ListTag tempCompoundList = new ListTag();
        for (ItemStack is : queue) {
            if (!is.isEmpty()) {
                tempCompoundList.add(is.save(new CompoundTag()));
            }
        }
        compound.put("queue", tempCompoundList);
        return compound;
    }
    @Override
    public void setRemoved() {
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), h.getStackInSlot(i))));
    }

    @Override
    public void tick() {
        // only run on server
        if (!level.isClientSide) {
            if(queue.size() > 0){
                ItemStack processIS = queue.get(queue.size() - 1);
                if(processIS != ItemStack.EMPTY) {
                    if(this.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                        if (progress < 100) {
                            if(getEnergy() >= YMConfig.CONFIG.energyEncoder.get()) {
                                CompoundTag nbt = this.inventory.getStackInSlot(1).getTag();
                                if (nbt != null) {
                                    if (nbt.contains("stored_items")) {
                                        ListTag list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
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
                            CompoundTag nbt = this.inventory.getStackInSlot(1).getTag();
                            if (nbt != null) {
                                if(nbt.contains("stored_items")) {
                                    ListTag list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
                                    if(list.size() < 8) {
                                        list.add(StringTag.valueOf(processIS.getItem().getRegistryName() + ""));
                                        nbt.put("stored_items", list);
                                    }
                                } else {
                                    ListTag list = new ListTag();
                                    list.add(StringTag.valueOf(processIS.getItem().getRegistryName() + ""));
                                    nbt.put("stored_items", list);
                                }
                            } else {
                                nbt = new CompoundTag();
                                ListTag list = new ListTag();
                                list.add(StringTag.valueOf(processIS.getItem().getRegistryName() + ""));
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
    public Component getDisplayName() {
        return new TranslatableComponent(ObjectHolders.ENCODER_BLOCK.getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player playerEntity) {
        return new EncoderContainer(windowID, level, worldPosition, playerInventory, playerEntity);
    }
}

