package realmayus.youmatter.scanner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YouMatter;

import java.util.Arrays;
import java.util.List;

public class ScannerScreen extends AbstractContainerScreen<ScannerContainer> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private ScannerTile te;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/scanner.png");

    public ScannerScreen(ScannerContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.te = container.te;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        int xAxis = (mouseX - (width - WIDTH) / 2);
        int yAxis = (mouseY - (height - HEIGHT) / 2);

        if (xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.gui.energy.title")), new TextComponent(I18n.get("youmatter.gui.energy.description", te.getClientEnergy()))));
        }

        if (!te.getHasEncoderClient()) {
            if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
                drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.warning.scanner1")), new TextComponent(I18n.get("youmatter.warning.scanner2")), new TextComponent(I18n.get("youmatter.warning.scanner3"))));
            }
        }

    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        drawEnergyBolt(matrixStack, te.getClientEnergy());
        drawProgressDisplayChain(matrixStack, te.getClientProgress());

        if(!te.getHasEncoderClient()) {
            this.blit(matrixStack, 16, 59, 176, 101, 16, 16);
        }
        font.draw(matrixStack, I18n.get(ObjectHolders.SCANNER_BLOCK.getDescriptionId()), 8, 6, 0x404040);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    private void drawProgressDisplayChain(PoseStack matrixStack, int progress) {
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

        this.minecraft.getTextureManager().bind(GUI);
        this.blit(matrixStack, 79, 62, 176, 41, Math.round((arrow / 100.0f) * 18), 12);
        this.blit(matrixStack, 104, 34, 176, 53, 17, Math.round((circuits / 100.0f) * 24));
        this.blit(matrixStack, 54, 34, 176, 77, 17, Math.round((circuits / 100.0f) * 24));
    }

    private void drawEnergyBolt(PoseStack matrixStack, int energy) {
        this.minecraft.getTextureManager().bind(GUI);

        if(energy == 0) {
            this.blit(matrixStack, 141, 35, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            this.blit(matrixStack, 141, 35, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }

    private void drawTooltip(PoseStack matrixStack, int x, int y, List<Component> tooltips) {
        renderComponentTooltip(matrixStack, tooltips, x, y);
    }
}
