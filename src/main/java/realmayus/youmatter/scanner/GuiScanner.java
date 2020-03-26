package realmayus.youmatter.scanner;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import realmayus.youmatter.YouMatter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiScanner extends GuiContainer {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private TileScanner te;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/scanner.png");

    public GuiScanner(TileScanner tileEntity, ContainerScanner container) {
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

        drawEnergyBolt(te.getClientEnergy());
        drawProgressDisplayChain(te.getClientProgress());

        //draw warning
        if(!te.getHasEncoderClient()) {
            drawTexturedModalRect(guiLeft + 16, guiTop + 59, 176, 101, 16, 16);
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);


        this.fontRenderer.drawString(I18n.format("youmatter.guiname.scanner"), 8, 6, 4210752);
    }

    private void drawProgressDisplayChain(int progress) {
        int circuits;
        int arrow;

        if(progress < 50) {
            circuits = progress * 2;
            arrow = 0;
        } else if(progress < 100) {
            circuits = 100;
            arrow = (progress -50) * 2;
        } else {
            circuits = 100;
            arrow = 100;
        }

        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(guiLeft + 79, guiTop + 63, 176, 41, Math.round((arrow / 100.0f) * 18), 12);
        drawTexturedModalRect(guiLeft + 104, guiTop + 35, 176, 53, 17, Math.round((circuits / 100.0f) * 24));
        drawTexturedModalRect(guiLeft + 54, guiTop + 35, 176, 77, 17, Math.round((circuits / 100.0f) * 24));
    }

    private void drawEnergyBolt(int energy) {
        mc.getTextureManager().bindTexture(GUI);

        if(energy == 0) {
            drawTexturedModalRect(guiLeft + 141, guiTop + 36, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100 / 1000000F;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            drawTexturedModalRect(guiLeft + 141, guiTop + 36, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

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

        if (xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.gui.energy.title"), I18n.format("youmatter.gui.energy.description", te.getClientEnergy())).collect(Collectors.toList()));
        }

        if (!te.getHasEncoderClient()) {
            if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
                drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.warning.scanner1"), I18n.format("youmatter.warning.scanner2"), I18n.format("youmatter.warning.scanner3")).collect(Collectors.toList()));

            }
        }
    }
    private void drawTooltip(int x, int y, List<String> tooltips) {
        drawHoveringText(tooltips, x, y, fontRenderer);
    }
}
