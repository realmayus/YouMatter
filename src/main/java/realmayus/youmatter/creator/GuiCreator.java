package realmayus.youmatter.creator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.client.FMLClientHandler;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketShowNext;
import realmayus.youmatter.network.PacketShowPrevious;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiCreator extends GuiContainer {

    public static final int WIDTH = 176;
    public static final int HEIGHT = 165;

    private TileCreator creator;

    private ContainerCreator container;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/creator_improved.png");

    public GuiCreator(TileCreator tileEntity, ContainerCreator container) {
        super(container);

        this.container = container;
        xSize = WIDTH;
        ySize = HEIGHT;

        creator = tileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawFluidTank(89, 20, creator.getUTank());
        drawFluidTank(31, 20, creator.getSTank());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //TODO remove dis when not needed anymore
        this.fontRenderer.drawString("Creator", 8, 6, 4210752);
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

            //TODO localize diz
            drawTooltip(mouseX, mouseY, Stream.of("Stabilizer", "Amount: " + creator.getSTank().getFluidAmount() + " mB").collect(Collectors.toList()));
        }
        if(xAxis >= 89 && xAxis <= 102 && yAxis >= 20 && yAxis <= 75) {

            //TODO localize diz
            drawTooltip(mouseX, mouseY, Stream.of("U-Matter", "Amount: " + creator.getUTank().getFluidAmount() + " mB").collect(Collectors.toList()));
        }
    }

    public void drawTooltip(int x, int y, List<String> tooltips)
    {
        drawHoveringText(tooltips, x, y, fontRenderer);
    }


    //both drawFluid and drawFluidTank is courtesy of DarkGuardsMan and was modified to suit my needs. Go check him out: https://github.com/BuiltBrokenModding/Atomic-Science | MIT License |  Copyright (c) 2018 Built Broken Modding License: https://opensource.org/licenses/MIT
    protected void drawFluid(int x, int y, int line, int col, int width, int drawSize, FluidStack fluidStack)
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

            if (texture != null)
            {
                //bind texture
                FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

                final int textureSize = 16;
                int start = 0;
                if (fluidIcon != null)
                {
                    int renderY = textureSize;
                    while (renderY != 0 && drawSize != 0)
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
        }
    }

    protected int meterHeight = 55;
    protected int meterWidth = 14;


    protected void drawFluidTank(int x, int y, IFluidTank tank)
    {

        //Get data
        final float scale = tank.getFluidAmount() / (float) tank.getCapacity();
        final FluidStack fluidStack = tank.getFluid();

        //Reset color
        GlStateManager.color(1, 1, 1, 1);


        //Draw fluid
        if (fluidStack != null)
        {
            this.drawFluid(this.guiLeft + x -1, this.guiTop + y, -3, 1, 14, (int) ((meterHeight - 1) * scale), fluidStack);
        }

        //Draw lines
        this.mc.renderEngine.bindTexture(GUI);
        this.drawTexturedModalRect(this.guiLeft + x, this.guiTop + y, 176, 35, meterWidth, meterHeight);

        //Reset color
        setColor(null);

    }

    protected void setColor(Color color)
    {
        if (color == null)
        {
            GlStateManager.color(1, 1, 1, 1);
        }
        else
        {
            GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        }
    }

    private void drawEnergyBar(int percentage) {
//        drawRect(guiLeft + 10, guiTop + 5, guiLeft + 112, guiTop + 15, 0xff555555);

        int linesToDraw = Math.toIntExact(Math.round(56 * (percentage / 100.0f)));
        for(int i = 0; i < linesToDraw - 1; i++) {
            if(i == 0 || i == 54){
                drawHorizontalLine(guiLeft + 11, guiLeft + 22, guiTop + 74 - i, 0xffad0000);
            } else {
                drawHorizontalLine(guiLeft + 10, guiLeft + 23, guiTop + 74 - i, i % 2 == 0 ? 0xffad0000 : 0xFF570000);
            }

        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            int xAxis = (mouseX - (width - xSize) / 2);
            int yAxis = (mouseY - (height - ySize) / 2);
            if(xAxis >= 80 && xAxis <= 85 && yAxis >= 21 && yAxis <= 31) {
                //Playing Click sound
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketShowPrevious());
            } else if(xAxis >= 108 && xAxis <= 113 && yAxis >= 21 && yAxis <= 31) {
                //Playing Click sound
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketShowNext() );
            }
        }
    }

}
