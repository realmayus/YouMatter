package realmayus.youmatter.creator;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import realmayus.youmatter.util.IGuiTile;

public class TileCreator extends TileEntity implements IGuiTile {
    @Override
    public Container createContainer(EntityPlayer player) {
        return null;
    }

    @Override
    public GuiContainer createGui(EntityPlayer player) {
        return null;
    }
}
