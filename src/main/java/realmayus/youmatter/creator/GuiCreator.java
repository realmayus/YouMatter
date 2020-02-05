package realmayus.youmatter.creator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.client.FMLClientHandler;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.network.PacketChangeSettingsCreatorServer;
import realmayus.youmatter.network.PacketHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiCreator extends GuiContainer {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private TileCreator te;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/creator.png");

    GuiCreator(TileCreator tileEntity, ContainerCreator container) {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        te = tileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawFluidTank(89, 23, te.getUTank());
        drawFluidTank(31, 23, te.getSTank());
    }

    private void drawActiveIcon(boolean isActive) {
        mc.getTextureManager().bindTexture(GUI);

        if(isActive) {
            drawTexturedModalRect(154, 13, 176, 24, 8, 9);

        } else {
            drawTexturedModalRect(154, 13, 176, 15, 8, 9);
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        mc.getTextureManager().bindTexture(GUI);

        if(te.getClientEnergy() == 0) {
            drawTexturedModalRect(150, 59, 176, 114, 15, 20);
        } else {
            double percentage = te.getClientEnergy() * 100 / 1000000;  // i know this is dumb
            float percentagef = (float)percentage / 100; // but it works.
            drawTexturedModalRect(150, 59, 176, 93, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
        drawActiveIcon(te.isActivatedClient());
        this.fontRenderer.drawString(I18n.format("youmatter.guiname.creator"), 8, 6, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //Render the dark background
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        //Render any tooltips
        renderHoveredToolTip(mouseX, mouseY);

        int xAxis = (mouseX - (width - xSize) / 2);
        int yAxis = (mouseY - (height - ySize) / 2);

        if(xAxis >= 31 && xAxis <= 44 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.gui.stabilizer.title"), I18n.format("youmatter.gui.stabilizer.description", te.getSTank().getFluidAmount())).collect(Collectors.toList()));
        }
        if(xAxis >= 89 && xAxis <= 102 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.gui.umatter.title"), I18n.format("youmatter.gui.umatter.description", te.getUTank().getFluidAmount())).collect(Collectors.toList()));
        }
        if(xAxis >= 150 && xAxis <= 164 && yAxis >= 57 && yAxis <= 77) {
            drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.gui.energy.title"), I18n.format("youmatter.gui.energy.description", te.getClientEnergy())).collect(Collectors.toList()));
        }
        if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
            drawTooltip(mouseX, mouseY, Stream.of(te.isActivatedClient() ? I18n.format("youmatter.gui.active") : I18n.format("youmatter.gui.paused"), I18n.format("youmatter.gui.clicktochange")).collect(Collectors.toList()));

        }
    }

    private void drawTooltip(int x, int y, List<String> tooltips)
    {
        drawHoveringText(tooltips, x, y, fontRenderer);
    }

    //both drawFluid and drawFluidTank is courtesy of DarkGuardsMan and was modified to suit my needs. Go check him out: https://github.com/BuiltBrokenModding/Atomic-Science | MIT License |  Copyright (c) 2018 Built Broken Modding License: https://opensource.org/licenses/MIT
    private void drawFluid(int x, int y, int line, int col, int width, int drawSize, FluidStack fluidStack)
    {
        if (fluidStack != null && fluidStack.getFluid() != null)
        {
            drawSize -= 1;
            ResourceLocation fluidIcon = null;
            Fluid fluid = fluidStack.getFluid();
            if (fluid != null)
            {
                if (fluid.getStill(fluidStack) != null)
                {
                    fluidIcon = fluid.getStill(fluidStack);
                }
                else if (fluid.getFlowing(fluidStack) != null)
                {
                    fluidIcon = fluid.getFlowing(fluidStack);
                }
                else
                {
                    fluidIcon = FluidRegistry.WATER.getStill();
                }
            }
            //Get sprite
            TextureAtlasSprite texture = FMLClientHandler.instance().getClient().getTextureMapBlocks().getAtlasSprite(fluidIcon.toString());
            //bind texture
            FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            final int textureSize = 16;
            int start = 0;
            int renderY;
            while (drawSize != 0)
            {
                if (drawSize > textureSize)
                {
                    renderY = textureSize;
                    drawSize -= textureSize;
                }
                else
                {
                    renderY = drawSize;
                    drawSize = 0;
                }

                this.drawTexturedModalRect(x + col, y + line + 58 - renderY - start, texture, width, textureSize - (textureSize - renderY));
                start = start + textureSize;
            }
        }
    }

    private void drawFluidTank(int x, int y, IFluidTank tank)
    {

        //Get data
        final float scale = tank.getFluidAmount() / (float) tank.getCapacity();
        final FluidStack fluidStack = tank.getFluid();

        //Reset color
        GlStateManager.color(1, 1, 1, 1);


        //Draw fluid
        int meterHeight = 55;
        if (fluidStack != null)
        {
            this.drawFluid(this.guiLeft + x -1, this.guiTop + y, -3, 1, 14, (int) ((meterHeight - 1) * scale), fluidStack);
        }

        //Draw lines
        this.mc.renderEngine.bindTexture(GUI);
        int meterWidth = 14;
        this.drawTexturedModalRect(this.guiLeft + x, this.guiTop + y, 176, 35, meterWidth, meterHeight);

        //Reset color
        GlStateManager.color(1, 1, 1, 1);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            int xAxis = (mouseX - (width - xSize) / 2);
            int yAxis = (mouseY - (height - ySize) / 2);
            if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
                //Playing Click sound
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketChangeSettingsCreatorServer(!te.isActivatedClient()));
            }
        }
    }

}
