package realmayus.youmatter.encoder;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.items.ThumbdriveItem;
import realmayus.youmatter.scanner.ContainerScanner;

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

        if (!(te.inputHandler.getStackInSlot(1).getItem() instanceof ThumbdriveItem)) {
            drawTexturedModalRect(16, 59, 176, 66, 16, 16);
        }

            //TODO Localize
        this.fontRenderer.drawString("Encoder", 8, 6, 4210752);
    }

    private void drawProgressDisplayChain(int progress) {
        int arrow1;
        int lock;
        int arrow2;

        if(progress < 33) {
            arrow1 = Math.round(progress * 3.03F);
            lock = 0;
            arrow2 = 0;
        } else if(progress < 66) {
            arrow1 = 100;
            lock = Math.round((progress - 33) * 3.03F);
            arrow2 = 0;
        } else if(progress < 99) {
            arrow1 = 100;
            lock = 100;
            arrow2 = Math.round((progress - 66) * 3.03F);
        } else {
            arrow1 = 100;
            lock = 100;
            arrow2 = 100;
        }

        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(22, 42, 176, 41, Math.round((arrow1 / 100.0f) * 18), 12);
        drawTexturedModalRect(47, 41, 176, 53, 7, Math.round((lock / 100.0f) * 13));
        drawTexturedModalRect(61, 42, 176, 41, Math.round((arrow2 / 100.0f) * 18), 12);

    }

    private void drawEnergyBolt(int energy) {
        mc.getTextureManager().bindTexture(GUI);

        if(energy == 0) {
            drawTexturedModalRect(141, 37, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100 / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
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

        if (!(te.inputHandler.getStackInSlot(1).getItem() instanceof ThumbdriveItem)) {
            if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
                //TODO localize diz
                drawTooltip(mouseX, mouseY, Stream.of("§cYou need to insert a thumbdrive first.").collect(Collectors.toList()));

            }
        }
    }

    private void drawTooltip(int x, int y, List<String> tooltips)
    {
        drawHoveringText(tooltips, x, y, fontRenderer);
    }

}
