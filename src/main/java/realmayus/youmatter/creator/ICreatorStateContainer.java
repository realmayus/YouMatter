package realmayus.youmatter.creator;

import net.minecraft.nbt.NBTTagCompound;

public interface ICreatorStateContainer {

    void sync(int uFluidAmount, int sFluidAmount, int energy, int progress, NBTTagCompound uTank, NBTTagCompound sTank, boolean isActivated);

}
