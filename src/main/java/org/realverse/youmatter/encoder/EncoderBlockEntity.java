package org.realverse.youmatter.encoder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.realverse.youmatter.ModContent;
import org.realverse.youmatter.YMConfig;
import org.realverse.youmatter.items.ThumbdriveItem;
import org.realverse.youmatter.util.MyEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EncoderBlockEntity extends BlockEntity implements MenuProvider {

    private List<ItemStack> queue = new ArrayList<>();
    public ItemStackHandler inventory;
    private final MyEnergyStorage myEnergyStorage;

    public EncoderBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.ENCODER_BLOCK_ENTITY.get(), pos, state);
        this.inventory = new ItemStackHandler(5) {
            protected void onContentsChanged(int slot) {
                EncoderBlockEntity.this.setChanged();
            }

            public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return slot == 1 ? super.insertItem(slot, stack, simulate) : stack;
            }
        };
        this.progress = 0;
        this.myEnergyStorage = new MyEnergyStorage(this, 1000000, Integer.MAX_VALUE);
    }

    // Calling this method signals incoming data from a neighboring scanner
    public void ignite(ItemStack itemStack) {
        if (itemStack != ItemStack.EMPTY && itemStack != null) {
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
        return this.myEnergyStorage.getEnergyStored();
    }

    public void setEnergy(int energy) {
        myEnergyStorage.setEnergy(energy);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        this.setProgress(compound.getInt("progress"));
        this.setEnergy(compound.getInt("energy"));
        if (compound.contains("inventory")) {
            this.inventory.deserializeNBT(provider, (CompoundTag) compound.get("inventory"));
        }
        if (compound.contains("queue")) {
            if (compound.get("queue") instanceof ListTag) {
                List<ItemStack> queueBuilder = new ArrayList<>();
                for (Tag base : compound.getList("queue", Tag.TAG_COMPOUND)) {
                    if (base instanceof CompoundTag nbtTagCompound) {
                        if (!ItemStack.parseOptional(provider, nbtTagCompound).isEmpty()) {
                            queueBuilder.add(ItemStack.parseOptional(provider, nbtTagCompound));
                        }
                    }
                }
                queue = queueBuilder;
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        if (inventory != null) {
            compound.put("inventory", this.inventory.serializeNBT(provider));
        }
        ListTag tempCompoundList = new ListTag();
        for (ItemStack is : queue) {
            if (!is.isEmpty()) {
                tempCompoundList.add(is.save(provider, new CompoundTag()));
            }
        }
        compound.put("queue", tempCompoundList);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.invalidateCapabilities();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EncoderBlockEntity be) {
        be.tick(level, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (queue.size() > 0) {
            ItemStack processIS = queue.get(queue.size() - 1);
            if (processIS != ItemStack.EMPTY) {
                if (inventory != null) {
                    if (inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                        if (progress < 100) {
                            if (getEnergy() >= YMConfig.get().energyEncoder) {
                                ItemContainerContents contents = inventory.getStackInSlot(1).get(DataComponents.CONTAINER);
                                if (contents != null) {
                                    List<ItemStack> list = contents.stream().toList();
                                    if (list.size() < YMConfig.get().thumbDriveSlots) {
                                        progress = progress + 1;
                                        myEnergyStorage.extractEnergy(YMConfig.get().energyEncoder, false);
                                    }
                                } else {
                                    progress = progress + 1; //doesn't have data stored yet
                                    myEnergyStorage.extractEnergy(YMConfig.get().energyEncoder, false);
                                }
                            }
                        } else {
                            ItemContainerContents contents = inventory.getStackInSlot(1).get(DataComponents.CONTAINER);
                            if (contents != null) {
                                List<ItemStack> list = new ArrayList<>(contents.stream().toList());
                                if (list.size() < YMConfig.get().thumbDriveSlots) {
                                    for (ItemStack stack : contents.nonEmptyItems()) {
                                        list.add(stack);
                                    }
                                    this.inventory.getStackInSlot(1).set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
                                }
                            } else {
                                List<ItemStack> list = new ArrayList<>();
                                list.add(processIS.getItem().getDefaultInstance());
                                this.inventory.getStackInSlot(1).set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
                            }
                        }
                        queue.remove(processIS);
                        progress = 0;
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModContent.ENCODER_BLOCK.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
        return new EncoderMenu(windowID, level, worldPosition, playerInventory, player);
    }

    public ItemStackHandler getItemHandler() {
        return this.inventory;
    }

    public IEnergyStorage getEnergyHandler() {
        return this.myEnergyStorage;
    }
}

