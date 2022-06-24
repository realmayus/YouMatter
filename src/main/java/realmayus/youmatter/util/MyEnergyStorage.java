package realmayus.youmatter.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.EnergyStorage;

public class MyEnergyStorage extends EnergyStorage {

    private final BlockEntity be;

    public MyEnergyStorage(BlockEntity be, int capacity, int maxReceive) {
        super(capacity, maxReceive, 0);
        this.be = be;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        be.setChanged();

        if (be.getLevel() != null && !be.getLevel().isClientSide) {
            be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyToExtract = Math.min(energy, maxExtract);
        if (!simulate && energyToExtract > 0) {
            energy -= energyToExtract;
            be.setChanged();

            if(be.getLevel() != null && !be.getLevel().isClientSide) {
                be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
        return energyToExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyToReceive = Math.min(capacity - energy, maxReceive);
        if (!simulate && energyToReceive > 0) {
            energy += energyToReceive;
            be.setChanged();

            if(be.getLevel() != null && !be.getLevel().isClientSide) {
                be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
        return energyToReceive;
    }
}
