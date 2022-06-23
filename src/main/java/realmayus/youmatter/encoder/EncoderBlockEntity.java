package realmayus.youmatter.encoder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.util.MyEnergyStorage;

public class EncoderBlockEntity extends BlockEntity implements MenuProvider {

    private List<ItemStack> queue = new ArrayList<>();

    public EncoderBlockEntity(BlockPos pos, BlockState state) {
        super(ObjectHolders.ENCODER_TILE, pos, state);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventory.cast();
        }

        if(cap == CapabilityEnergy.ENERGY) {
            return myEnergyStorage.cast();

        }
        return super.getCapability(cap, side);
    }

    public LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            EncoderBlockEntity.this.setChanged();
        }
    });


    // Calling this method signals incoming data from a neighboring scanner
    public void ignite(ItemStack itemStack) {
        if(itemStack != ItemStack.EMPTY && itemStack != null) {
            queue.add(itemStack);
            setChanged();
        }
    }

    private int progress = 0;


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setChanged();
    }

    public int getEnergy() {
        return myEnergyStorage.resolve().get().getEnergyStored();
    }

    public void setEnergy(int energy) {
        myEnergyStorage.resolve().get().setEnergy(energy);
    }

    private LazyOptional<MyEnergyStorage> myEnergyStorage = LazyOptional.of(() -> new MyEnergyStorage(this, 1000000, Integer.MAX_VALUE));

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        setProgress(compound.getInt("progress"));
        setEnergy(compound.getInt("energy"));
        if(compound.contains("inventory")) {
            inventory.resolve().get().deserializeNBT((CompoundTag) compound.get("inventory"));
        }
        if(compound.contains("queue")) {
            if (compound.get("queue") instanceof ListTag) {
                List<ItemStack> queueBuilder = new ArrayList<>();
                for(Tag base: compound.getList("queue", Tag.TAG_COMPOUND)) {
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
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        if (inventory != null) {
            compound.put("inventory", inventory.resolve().get().serializeNBT());
        }
        ListTag tempCompoundList = new ListTag();
        for (ItemStack is : queue) {
            if (!is.isEmpty()) {
                tempCompoundList.add(is.save(new CompoundTag()));
            }
        }
        compound.put("queue", tempCompoundList);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventory.invalidate();
        myEnergyStorage.invalidate();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EncoderBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(queue.size() > 0){
            ItemStack processIS = queue.get(queue.size() - 1);
            if(processIS != ItemStack.EMPTY) {
                inventory.ifPresent(inventory -> {
                    if(inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                        if (progress < 100) {
                            if(getEnergy() >= YMConfig.CONFIG.energyEncoder.get()) {
                                CompoundTag nbt = inventory.getStackInSlot(1).getTag();
                                if (nbt != null) {
                                    if (nbt.contains("stored_items")) {
                                        ListTag list = nbt.getList("stored_items", Tag.TAG_STRING);
                                        if (list.size() < 8) {
                                            progress = progress + 1;
                                            myEnergyStorage.ifPresent(myEnergyStorage -> myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyEncoder.get(), false));
                                        }
                                    }
                                } else {
                                    progress = progress + 1; //doesn't have data stored yet
                                    myEnergyStorage.ifPresent(myEnergyStorage -> myEnergyStorage.extractEnergy(YMConfig.CONFIG.energyEncoder.get(), false));
                                }
                            }
                        } else {
                            CompoundTag nbt = inventory.getStackInSlot(1).getTag();
                            if (nbt != null) {
                                if(nbt.contains("stored_items")) {
                                    ListTag list = nbt.getList("stored_items", Tag.TAG_STRING);
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
                                inventory.getStackInSlot(1).setTag(nbt);
                            }

                            queue.remove(processIS);
                            progress = 0;
                        }
                    }
                });
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(ObjectHolders.ENCODER_BLOCK.getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new EncoderMenu(windowID, level, worldPosition, playerInventory, player);
    }
}

