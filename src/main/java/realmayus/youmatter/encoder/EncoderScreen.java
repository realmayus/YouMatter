package realmayus.youmatter.encoder;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.items.ThumbdriveItem;


public class EncoderScreen extends AbstractContainerScreen<EncoderContainer> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private EncoderTile te;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/encoder.png");

    public EncoderScreen(EncoderContainer container, Inventory inv, Component name) {
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

        if(xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.gui.energy.title")), new TextComponent(I18n.get("youmatter.gui.energy.description", te.getEnergy()))));
        }
        if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
            if (te.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                CompoundTag nbt = te.inventory.getStackInSlot(1).getTag();
                if (nbt != null) {
                    ListTag list = nbt.getList("stored_items", Tag.TAG_STRING);
                    if (list.size() >= 8) {
                        drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.warning.encoder2"))));
                    }
                }
            } else {
                drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.warning.encoder1"))));
            }
        }
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem._setShaderTexture(0, GUI);

        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        drawEnergyBolt(matrixStack, te.getEnergy());
        drawProgressDisplayChain(matrixStack, te.getProgress());

        if (!(te.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem)) {
            this.blit(matrixStack, 16, 59, 176, 66, 16, 16);
        } else {
            CompoundTag nbt = te.inventory.getStackInSlot(1).getTag();
            if (nbt != null) {
                ListTag list = nbt.getList("stored_items", Tag.TAG_STRING);
                if (list.size() >= 8) {
                    this.blit(matrixStack, 16, 59, 176, 66, 16, 16);
                }
            }
        }
        font.draw(matrixStack, I18n.get(ObjectHolders.ENCODER_BLOCK.getDescriptionId()), 8, 6, 0x404040);
    }

    private void drawProgressDisplayChain(PoseStack matrixStack, int progress) {
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

        RenderSystem._setShaderTexture(0, GUI);
        this.blit(matrixStack, 22, 41, 176, 41, Math.round((arrow1 / 100.0f) * 18), 12);
        this.blit(matrixStack, 47, 40, 176, 53, 7, Math.round((lock / 100.0f) * 13));
        this.blit(matrixStack, 61, 41, 176, 41, Math.round((arrow2 / 100.0f) * 18), 12);

    }

    private void drawEnergyBolt(PoseStack matrixStack, int energy) {
        RenderSystem._setShaderTexture(0, GUI);

        if(energy == 0) {
            this.blit(matrixStack, 141, 36, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            this.blit(matrixStack, 141, 36, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.
        }
    }

    private void drawTooltip(PoseStack matrixStack, int x, int y, List<Component> tooltips) {
        renderComponentTooltip(matrixStack, tooltips, x, y);
    }
}
