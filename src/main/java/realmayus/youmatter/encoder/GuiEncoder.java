package realmayus.youmatter.encoder;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import realmayus.youmatter.YouMatter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiEncoder extends GuiContainer {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 165;

    private TileEncoder te;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/encoder.png");

    GuiEncoder(TileEncoder tileEntity, ContainerEncoder container) {
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
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        drawEnergyBolt(te.getClientEnergy());
        drawProgressDisplayChain(te.getClientProgress());

        //TODO Localize
        this.fontRenderer.drawString("Encoder", 8, 6, 4210752);
    }

    private void drawProgressDisplayChain(int progress) {
        int firstArrow = progress - 33.3f;

        mc.getTextureManager().bindTexture(GUI);
        System.out.println(progress);
        float percentagef = (float) progress / 100;
        System.out.println(percentagef);
        drawTexturedModalRect(22, 42, 176, 41, Math.round(18 * percentagef), 12); // it's not really intended that the bolt fills from the top but it looks cool tbh.

    }

    private void drawEnergyBolt(int energy) {
        mc.getTextureManager().bindTexture(GUI);

        if(energy == 0) {
            drawTexturedModalRect(141, 37, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100 / 1000000;  // i know this is dumb
            float percentagef = (float)percentage / 100; // but it works.
            drawTexturedModalRect(141, 37, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground(); //Render the dark background
        super.drawScreen(mouseX, mouseY, partialTicks);

        //Render any tooltips
        renderHoveredToolTip(mouseX, mouseY);

        int xAxis = (mouseX - (width - xSize) / 2);
        int yAxis = (mouseY - (height - ySize) / 2);

        if(xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {

            //TODO localize diz
            drawTooltip(mouseX, mouseY, Stream.of("§6Energy", "Stored: " + te.getClientEnergy() + " FE").collect(Collectors.toList()));
        }
    }

    private void drawTooltip(int x, int y, List<String> tooltips)
    {
        drawHoveringText(tooltips, x, y, fontRenderer);
    }

}
