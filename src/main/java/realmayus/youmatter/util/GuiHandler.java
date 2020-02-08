package realmayus.youmatter.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import realmayus.youmatter.creator.ContainerCreator;
import realmayus.youmatter.creator.GuiCreator;
import realmayus.youmatter.creator.TileCreator;
import realmayus.youmatter.encoder.ContainerEncoder;
import realmayus.youmatter.encoder.GuiEncoder;
import realmayus.youmatter.encoder.TileEncoder;
import realmayus.youmatter.replicator.ContainerReplicator;
import realmayus.youmatter.replicator.GuiReplicator;
import realmayus.youmatter.replicator.TileReplicator;
import realmayus.youmatter.scanner.ContainerScanner;
import realmayus.youmatter.scanner.GuiScanner;
import realmayus.youmatter.scanner.TileScanner;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileScanner) {
            return new ContainerScanner(player.inventory, (TileScanner)te);
        } else if (te instanceof TileReplicator) {
            return new ContainerReplicator(player.inventory, (TileReplicator) te);
        } else if (te instanceof TileEncoder) {
            return new ContainerEncoder(player.inventory, (TileEncoder) te);
        } else if (te instanceof TileCreator) {
            return new ContainerCreator(player.inventory, (TileCreator) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileScanner) {
            return new GuiScanner((TileScanner) te, new ContainerScanner(player.inventory, (TileScanner) te));
        } else if (te instanceof TileReplicator) {
            return new GuiReplicator((TileReplicator) te, new ContainerReplicator(player.inventory, (TileReplicator) te));
        } else if (te instanceof TileEncoder) {
            return new GuiEncoder((TileEncoder) te, new ContainerEncoder(player.inventory, (TileEncoder) te));
        } else if (te instanceof TileCreator) {
            return new GuiCreator((TileCreator) te, new ContainerCreator(player.inventory, (TileCreator) te));
        }
        return null;
    }
}
