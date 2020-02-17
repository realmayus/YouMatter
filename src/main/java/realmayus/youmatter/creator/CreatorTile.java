package realmayus.youmatter.creator;


import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.util.MyEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CreatorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public CreatorTile() {
        super(ObjectHolders.CREATOR_TILE);
    }

    private static final int MAX_UMATTER = 11000;
    private static final int MAX_STABILIZER = 11000;

    private boolean isActivatedClient = true;
    private boolean isActivated = true;

    boolean isActivatedClient() {
        return isActivatedClient;
    }

    void setActivatedClient(boolean activatedClient) {
        isActivatedClient = activatedClient;
    }

    boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
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
            CreatorTile.this.markDirty();
        }
    };
    
    private FluidTank uTank = new FluidTank(MAX_UMATTER) {
        @Override
        protected void onContentsChanged() {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    };

    private FluidTank sTank = new FluidTank(MAX_STABILIZER) {
        @Override
        protected void onContentsChanged() {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    }; 

    FluidTank getUTank() {
        return uTank;

    }

    FluidTank getSTank() {
        return sTank;
    }

    private IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 2;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (tank == 0) {
                return uTank.getFluid();
            } else if (tank == 1) {
                return sTank.getFluid();
            }
            return null;
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank == 0) {
                return MAX_UMATTER;
            } else if (tank == 1) {
                return MAX_STABILIZER;
            }
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            if (tank == 0) {
                return false;
            } else if (tank == 1 && stack.getFluid().equals(ModFluids.stabilizer.get())) {
                return true;
            }
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.getFluid().equals(ModFluids.stabilizer.get())) {
                if (MAX_STABILIZER - getSTank().getFluidAmount() < resource.getAmount()) {
                    return sTank.fill(new FluidStack(resource.getFluid(), MAX_STABILIZER), action);
                } else {
                    return sTank.fill(resource, action);
                }
            }
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.getFluid().equals(ModFluids.umatter.get())) {
                if (uTank.getFluidAmount() < resource.getAmount()) {
                    uTank.drain(uTank.getFluid(), action);
                    return uTank.getFluid();
                } else {
                    uTank.drain(resource, action);
                    return resource;
                }
            }
            return null;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if(uTank.getFluid().getFluid() != null) {
                return uTank.drain(uTank.getFluid(), action);
            } else {
                return null;
            }
        }
    };


    boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public int getClientProgress() {
        return clientProgress;
    }

    void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }
    
    void setClientUFluidAmount(int clientUFluidAmount) {
        this.clientUFluidAmount = clientUFluidAmount;
    }
    void setClientSFluidAmount(int clientSFluidAmount) {
        this.clientSFluidAmount = clientSFluidAmount;
    }

    private int clientUFluidAmount = -1;
    private int clientSFluidAmount = -1;

    int getClientEnergy() {
        return clientEnergy;
    }

    void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    public int getEnergy() {
        return myEnergyStorage.getEnergyStored();
    }

    public void setClientTank(CompoundNBT tank) {
        FluidTank newUTank = new FluidTank(MAX_UMATTER).readFromNBT(tank);
        FluidTank newSTank = new FluidTank(MAX_STABILIZER).readFromNBT(tank);
        getUTank().setFluid(newUTank.getFluid());
        getSTank().setFluid(newSTank.getFluid());
    }


    private int clientEnergy = -1;
    private int clientProgress = -1;

    private MyEnergyStorage myEnergyStorage = new MyEnergyStorage(1000000, Integer.MAX_VALUE);

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        CompoundNBT tagUTank = compound.getCompound("uTank");
        CompoundNBT tagSTank = compound.getCompound("sTank");
        uTank.readFromNBT(tagUTank);
        sTank.readFromNBT(tagSTank);
        myEnergyStorage.setEnergy(compound.getInt("energy"));
        isActivated = compound.getBoolean("isActivated");
        if(compound.contains("inventory")) {
            inventory.deserializeNBT((CompoundNBT) compound.get("inventory"));
        }
    }


    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        CompoundNBT tagSTank = new CompoundNBT();
        CompoundNBT tagUTank = new CompoundNBT();
        sTank.writeToNBT(tagSTank);
        uTank.writeToNBT(tagUTank);
        compound.put("uTank", tagUTank);
        compound.put("sTank", tagSTank);
        compound.putInt("energy", getEnergy());
        compound.putBoolean("isActivated", isActivated);
        if(compound.contains("inventory")) {
            inventory.deserializeNBT((CompoundNBT) compound.get("inventory"));
        }
        return compound;
    }

    void setClientUTank(CompoundNBT tank) {
        FluidTank newTank = new FluidTank(10500).readFromNBT(tank);
        getUTank().setFluid(newTank.getFluid());
        getUTank().setCapacity(newTank.getCapacity());
    }

    void setClientSTank(CompoundNBT tank) {
        FluidTank newTank = new FluidTank(10500).readFromNBT(tank);
        getSTank().setFluid(newTank.getFluid());
        
        getSTank().setCapacity(newTank.getCapacity());
    }

    private int currentPartTick = 0;

    @Override
    public void tick() {
        if (currentPartTick == 40) { // every 2 sec
            if (!world.isRemote) {
                if(isActivated()) {
                    if (getEnergy() >= 0.3f * 1000000 && sTank.getFluidAmount() >= 125) { // if energy more than 10 % of max energy
                        sTank.drain(125, IFluidHandler.FluidAction.EXECUTE);
                        uTank.fill(new FluidStack(ModFluids.umatter.get(), Math.round((float) 0.000005 * (getEnergy()/3f))), IFluidHandler.FluidAction.EXECUTE);
                        myEnergyStorage.consumePower(Math.round(getEnergy()/3f));
                    }
                }

                //Auto-outputting U-Matter
                Object[] neighborTE = getNeighborTileEntity(pos);
                if(neighborTE != null){
                    if (uTank.getFluidAmount() >= 500) {
                        uTank.drain(world.getTileEntity((BlockPos)neighborTE[0]).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, (Direction)neighborTE[1]).map(h -> h.fill(new FluidStack(ModFluids.umatter.get(), 500), IFluidHandler.FluidAction.EXECUTE)).orElse(0), IFluidHandler.FluidAction.EXECUTE);
                    } else {
                        uTank.drain(world.getTileEntity((BlockPos)neighborTE[0]).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, (Direction)neighborTE[1]).map(h -> h.fill(new FluidStack(ModFluids.umatter.get(), uTank.getFluidAmount()), IFluidHandler.FluidAction.EXECUTE)).orElse(0), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
            currentPartTick = 0;
        } else if ((currentPartTick % 5) == 0) { // every five ticks
            if (!world.isRemote) {
                if (!this.inventory.getStackInSlot(3).isEmpty()) {
                    if (this.inventory.getStackInSlot(3).getItem() instanceof BucketItem) {
                        if (getUTank().getFluidAmount() >= 1000) {
                            getUTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                            this.inventory.setStackInSlot(3, ItemStack.EMPTY);
                            this.inventory.insertItem(4, new ItemStack(ObjectHolders.UMATTER_BUCKET, 1), false);
                        }
                    }
                }
                if (!this.inventory.getStackInSlot(1).isEmpty()) {
                    BucketItem bucket = (BucketItem) this.inventory.getStackInSlot(1).getItem();
                    if (bucket == ObjectHolders.STABILIZER_BUCKET) {
                        if (getSTank().getFluidAmount() < getSTank().getCapacity()) {
                            getSTank().fill(new FluidStack(ModFluids.stabilizer.get(), 1000), IFluidHandler.FluidAction.EXECUTE);
                            this.inventory.setStackInSlot(1, ItemStack.EMPTY);
                            this.inventory.insertItem(2, new ItemStack(Items.BUCKET, 1), false);
                        }
                    }
                }
            }
            currentPartTick++;
        } else {
            currentPartTick++;
        }
    }

    private Object[] getNeighborTileEntity(BlockPos creatorPos) {
        HashMap<BlockPos, Direction> foundPos = new HashMap<>();
        for(Direction facing : Direction.values()) {
            if(world.getTileEntity(creatorPos.offset(facing)) != null) {
                Objects.requireNonNull(world.getTileEntity(creatorPos.offset(facing))).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing).ifPresent(x -> foundPos.put(creatorPos.offset(facing), facing));

            }
        }

        // Prioritize Replicator
        for (Map.Entry<BlockPos, Direction> entry : foundPos.entrySet()) {
           // if (world.getTileEntity( entry.getKey()) instanceof TileReplicator) {
                if(Objects.requireNonNull(world.getTileEntity(entry.getKey())).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, entry.getValue()).map(h -> h.fill(new FluidStack(ModFluids.umatter.get(), 500), IFluidHandler.FluidAction.SIMULATE)).orElse(0) > 0) {
                    //Replicator can take fluid
                    return new Object[] {entry.getKey(), entry.getValue()}; // position, facing
                }
            //}
        }

        // Replicator not found / can't take fluid, now trying other blocks
        for (Map.Entry<BlockPos, Direction> entry : foundPos.entrySet()) {
            if(Objects.requireNonNull(world.getTileEntity(entry.getKey())).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, entry.getValue()).map(h -> h.fill(new FluidStack(ModFluids.umatter.get(), 500), IFluidHandler.FluidAction.SIMULATE)).orElse(0) > 0) {
                //Tile can take fluid
                return new Object[] {entry.getKey(), entry.getValue()}; // position, facing
            }
        }

        // found nothing
        return null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ObjectHolders.CREATOR_BLOCK.getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new CreatorContainer(windowID, world, pos, playerInventory, playerEntity);

    }
}
