package realmayus.youmatter.scanner;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YMConfig;
import realmayus.youmatter.encoder.EncoderBlock;
import realmayus.youmatter.encoder.EncoderTile;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.IntStream;

public class ScannerTile extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

    public boolean hasEncoder = false;

    public ScannerTile() {
        super(ObjectHolders.SCANNER_TILE);
    }

    public boolean getHasEncoder() {
        return hasEncoder;
    }

    public void setHasEncoder(boolean hasEncoder) {
        this.hasEncoder = hasEncoder;
    }

    public boolean getHasEncoderClient() {
        return hasEncoderClient;
    }

    public void setHasEncoderClient(boolean hasEncoderClient) {
        this.hasEncoderClient = hasEncoderClient;
    }

    public boolean hasEncoderClient = false;

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
            ScannerTile.this.setChanged();
        }
    };

    private int clientEnergy = -1;
    private int clientProgress = -1;
    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getClientProgress() {
        return clientProgress;
    }

    public void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }

    public int getClientEnergy() {
        return clientEnergy;
    }

    public void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, Integer.MAX_VALUE);

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        if (compound.contains("progress")) {
            setProgress(compound.getInt("progress"));
        }
        if (compound.contains("energy")) {
            myEnergyStorage.setEnergy(compound.getInt("energy"));
        }
        if(compound.contains("inventory")) {
            inventory.deserializeNBT((CompoundNBT) compound.get("inventory"));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.putInt("progress", getProgress());
        compound.putInt("energy", getEnergy());
        if (inventory != null) {
            compound.put("inventory", inventory.serializeNBT());
        }

        return compound;
    }

    private int currentPartTick = 0;
    @Override
    public void tick() {
        if(currentPartTick >= 2) {
            if (getNeighborEncoder(this.worldPosition) != null) {
                hasEncoder = true;
                BlockPos encoderPos = getNeighborEncoder(this.worldPosition);
                if(!inventory.getStackInSlot(1).isEmpty() && isItemAllowed(inventory.getStackInSlot(1))) {
                    if(getEnergy() > YMConfig.CONFIG.energyScanner.get()) {
                        if (getProgress() < 100) {
                            setProgress(getProgress() + 1);
                            myEnergyStorage.consumePower(YMConfig.CONFIG.energyScanner.get());
                       } else if (encoderPos != null) {
                            // Notifying the neighboring encoder of this scanner having finished its operation
                            ((EncoderTile)level.getBlockEntity(encoderPos)).ignite(this.inventory.getStackInSlot(1)); //don't worry, this is already checked by getNeighborEncoder() c:
                            inventory.setStackInSlot(1, ItemStack.EMPTY);
                            setProgress(0);
                        }
                    }
                } else {
                    setProgress(0); // if item was suddenly removed, reset progress to 0
                }
            } else {
                hasEncoder = false;
            }
            currentPartTick = 0;
        } else {
            currentPartTick++;
        }
    }

    private boolean isItemAllowed(ItemStack itemStack) {

        boolean matches = YMConfig.CONFIG.filterItems.get().stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(itemStack.getItem().getRegistryName()).toString()));
            //If list should act as a blacklist AND it contains the item, disallow scanning
        if (YMConfig.CONFIG.filterMode.get() && matches) {
            return false;
            //If list should act as a whitelist AND it DOESN'T contain the item, disallow scanning
        } else if (YMConfig.CONFIG.filterMode.get() || matches) return true;
        else return false;
    }

    @Override
    public void setRemoved() {
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> IntStream.range(0, h.getSlots()).forEach(i -> InventoryHelper.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), h.getStackInSlot(i))));
    }

    @Nullable
    private BlockPos getNeighborEncoder(BlockPos scannerPos) {
        for(Direction facing : Direction.values()) {
            if(level.getBlockState(scannerPos.relative(facing)).getBlock() instanceof EncoderBlock) {
                if(level.getBlockEntity(scannerPos.relative(facing)) != null) {
                    if(level.getBlockEntity(scannerPos.relative(facing)) instanceof EncoderTile) {
                        return scannerPos.relative(facing);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ObjectHolders.SCANNER_BLOCK.getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ScannerContainer(windowID, level, worldPosition, playerInventory, playerEntity);
    }
}
