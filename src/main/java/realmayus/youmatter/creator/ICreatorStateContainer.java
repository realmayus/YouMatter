package realmayus.youmatter.creator;


import net.minecraftforge.fluids.FluidStack;

public interface ICreatorStateContainer {

    void sync(int energy, int progress, FluidStack uTank, FluidStack sTank, boolean isActivated);

}
