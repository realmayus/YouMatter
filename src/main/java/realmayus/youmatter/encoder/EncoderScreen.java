package realmayus.youmatter.encoder;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.items.ThumbdriveItem;

import java.util.Arrays;
import java.util.List;


public class EncoderScreen extends ContainerScreen<EncoderContainer> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private EncoderTile te;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/encoder.png");

    public EncoderScreen(EncoderContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.te = container.te;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        int xAxis = (mouseX - (width - WIDTH) / 2);
        int yAxis = (mouseY - (height - HEIGHT) / 2);

        if(xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new StringTextComponent(I18n.format("youmatter.gui.energy.title")), new StringTextComponent(I18n.format("youmatter.gui.energy.description", te.getClientEnergy()))));
        }
        if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
            if (te.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                CompoundNBT nbt = te.inventory.getStackInSlot(1).getTag();
                if (nbt != null) {
                    ListNBT list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
                    if (list.size() >= 8) {
                        drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new StringTextComponent(I18n.format("youmatter.warning.encoder2"))));
                    }
                }
            } else {
                drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new StringTextComponent(I18n.format("youmatter.warning.encoder1"))));
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);

        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawEnergyBolt(matrixStack, te.getClientEnergy());
        drawProgressDisplayChain(matrixStack, te.getClientProgress());

        if (!(te.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem)) {
            this.blit(matrixStack, 16, 59, 176, 66, 16, 16);
        } else {
            CompoundNBT nbt = te.inventory.getStackInSlot(1).getTag();
            if (nbt != null) {
                ListNBT list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
                if (list.size() >= 8) {
                    this.blit(matrixStack, 16, 59, 176, 66, 16, 16);
                }
            }
        }
        font.drawString(matrixStack, I18n.format(ObjectHolders.ENCODER_BLOCK.getTranslationKey()), 8, 6, 0x404040);
    }

    private void drawProgressDisplayChain(MatrixStack matrixStack, int progress) {
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

        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(matrixStack, 22, 41, 176, 41, Math.round((arrow1 / 100.0f) * 18), 12);
        this.blit(matrixStack, 47, 40, 176, 53, 7, Math.round((lock / 100.0f) * 13));
        this.blit(matrixStack, 61, 41, 176, 41, Math.round((arrow2 / 100.0f) * 18), 12);

    }

    private void drawEnergyBolt(MatrixStack matrixStack, int energy) {
        this.minecraft.getTextureManager().bindTexture(GUI);

        if(energy == 0) {
            this.blit(matrixStack, 141, 36, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            this.blit(matrixStack, 141, 36, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.
        }
    }

    private void drawTooltip(MatrixStack matrixStack, int x, int y, List<ITextComponent> tooltips) {
        func_243308_b(matrixStack, tooltips, x, y);
    }
}
