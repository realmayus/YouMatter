package realmayus.youmatter.replicator;

import net.minecraft.nbt.NBTTagCompound;

public interface IReplicatorStateContainer {

    void sync(int fluidAmount, int energy, int progress, NBTTagCompound tank);

}
