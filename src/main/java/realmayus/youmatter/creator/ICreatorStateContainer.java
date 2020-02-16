package realmayus.youmatter.creator;


import net.minecraft.nbt.CompoundNBT;

public interface ICreatorStateContainer {

    void sync(int uFluidAmount, int sFluidAmount, int energy, int progress, CompoundNBT uTank, CompoundNBT sTank, boolean isActivated);

}
