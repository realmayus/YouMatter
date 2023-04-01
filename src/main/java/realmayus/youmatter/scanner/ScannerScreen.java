package realmayus.youmatter.scanner;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import realmayus.youmatter.ModContent;
import realmayus.youmatter.YouMatter;

public class ScannerScreen extends AbstractContainerScreen<ScannerMenu> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private ScannerBlockEntity scanner;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/scanner.png");

    public ScannerScreen(ScannerMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.scanner = container.scanner;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);

        int xAxis = (mouseX - (width - WIDTH) / 2);
        int yAxis = (mouseY - (height - HEIGHT) / 2);

        if (xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(poseStack, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.energy.title")), Component.literal(I18n.get("youmatter.gui.energy.description", scanner.getEnergy()))));
        }

        if (!scanner.getHasEncoder()) {
            if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
                drawTooltip(poseStack, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.warning.scanner1")), Component.literal(I18n.get("youmatter.warning.scanner2")), Component.literal(I18n.get("youmatter.warning.scanner3"))));
            }
        }

    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        drawEnergyBolt(poseStack, scanner.getEnergy());
        drawProgressDisplayChain(poseStack, scanner.getProgress());

        if(!scanner.getHasEncoder()) {
            GuiComponent.blit(poseStack, 16, 59, 176, 101, 16, 16);
        }
        font.draw(poseStack, I18n.get(ModContent.SCANNER_BLOCK.get().getDescriptionId()), 8, 6, 0x404040);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem._setShaderTexture(0, GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        GuiComponent.blit(poseStack, relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    private void drawProgressDisplayChain(PoseStack poseStack, int progress) {
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

        RenderSystem._setShaderTexture(0, GUI);
        GuiComponent.blit(poseStack, 79, 62, 176, 41, Math.round((arrow / 100.0f) * 18), 12);
        GuiComponent.blit(poseStack, 104, 34, 176, 53, 17, Math.round((circuits / 100.0f) * 24));
        GuiComponent.blit(poseStack, 54, 34, 176, 77, 17, Math.round((circuits / 100.0f) * 24));
    }

    private void drawEnergyBolt(PoseStack poseStack, int energy) {
        RenderSystem._setShaderTexture(0, GUI);

        if(energy == 0) {
            GuiComponent.blit(poseStack, 141, 35, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            GuiComponent.blit(poseStack, 141, 35, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }

    private void drawTooltip(PoseStack poseStack, int x, int y, List<Component> tooltips) {
        renderComponentTooltip(poseStack, tooltips, x, y);
    }
}
