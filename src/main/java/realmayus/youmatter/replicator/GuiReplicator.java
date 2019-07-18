package realmayus.youmatter.replicator;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.client.FMLClientHandler;
import realmayus.youmatter.ModFluids;
import realmayus.youmatter.YouMatter;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiReplicator extends GuiContainer {

    public static final int WIDTH = 176;
    public static final int HEIGHT = 165;

    private TileReplicator replicator;

    private ContainerReplicator container;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/replicator.png");

    public GuiReplicator(TileReplicator tileEntity, ContainerReplicator container) {
        super(container);

        this.container = container;
        xSize = WIDTH;
        ySize = HEIGHT;

        replicator = tileEntity;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        drawFluidTank(26, 20, replicator.getTank(), Color.blue);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //TODO remove dis when not needed anymore
        this.fontRenderer.drawString(replicator.getTank().getFluidAmount() + "mB, " + replicator.getClientProgress() + "%, " + replicator.getClientEnergy() + "RF", 8, 6, 4210752);
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

        if(xAxis > 26 && xAxis < 39 && yAxis > 20 && yAxis < 75) {

            //TODO localize diz
            drawTooltip(mouseX, mouseY, Stream.of("U-Matter", "Amount: " + replicator.getTank().getFluidAmount() + "mB").collect(Collectors.toList()));
        }
    }

    public void drawTooltip(int x, int y, List<String> tooltips)
    {
        drawHoveringText(tooltips, x, y, fontRenderer);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

    }


    protected void drawFluid(int x, int y, int line, int col, int width, int drawSize, FluidStack fluidStack)
    {
        if (fluidStack != null && fluidStack.getFluid() != null)
        {
            drawSize -= 1; //TODO why?

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


    protected void drawFluidTank(int x, int y, IFluidTank tank, Color edgeColor)
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

}
