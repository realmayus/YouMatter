package realmayus.youmatter.encoder;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.items.ThumbdriveItem;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int xAxis = (mouseX - (width - WIDTH) / 2);
        int yAxis = (mouseY - (height - HEIGHT) / 2);

        if(xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.gui.energy.title"), I18n.format("youmatter.gui.energy.description", te.getClientEnergy())).collect(Collectors.toList()));
        }

        if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
            if (te.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                CompoundNBT nbt = te.inventory.getStackInSlot(1).getTag();
                if (nbt != null) {
                    ListNBT list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
                    if (list.size() >= 8) {
                        drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.warning.encoder2")).collect(Collectors.toList()));
                    }
                }
            } else {
                drawTooltip(mouseX, mouseY, Stream.of(I18n.format("youmatter.warning.encoder1")).collect(Collectors.toList()));
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawEnergyBolt(te.getClientEnergy());
        drawProgressDisplayChain(te.getClientProgress());

        if (!(te.inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem)) {
            this.blit(16, 59, 176, 66, 16, 16);
        } else {
            CompoundNBT nbt = te.inventory.getStackInSlot(1).getTag();
            if (nbt != null) {
                ListNBT list = nbt.getList("stored_items", Constants.NBT.TAG_STRING);
                if (list.size() >= 8) {
                    this.blit(16, 59, 176, 66, 16, 16);
                }
            }
        }
        font.drawString(I18n.format(ObjectHolders.ENCODER_BLOCK.getTranslationKey()), 8, 6, 0x404040);
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

        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(22, 41, 176, 41, Math.round((arrow1 / 100.0f) * 18), 12);
        this.blit(47, 40, 176, 53, 7, Math.round((lock / 100.0f) * 13));
        this.blit(61, 41, 176, 41, Math.round((arrow2 / 100.0f) * 18), 12);

    }

    private void drawEnergyBolt(int energy) {
        this.minecraft.getTextureManager().bindTexture(GUI);

        if(energy == 0) {
            this.blit(141, 36, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            this.blit(141, 36, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }

    private void drawTooltip(int x, int y, List<String> tooltips) {
        renderTooltip(tooltips, x, y);
    }
}
