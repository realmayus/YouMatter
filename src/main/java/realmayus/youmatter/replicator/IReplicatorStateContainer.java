package realmayus.youmatter.replicator;


import net.minecraftforge.fluids.FluidStack;

public interface IReplicatorStateContainer {

    void sync(int energy, int progress, FluidStack tank, boolean isActivated, boolean mode);

}
