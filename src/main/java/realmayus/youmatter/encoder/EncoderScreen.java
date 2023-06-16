package realmayus.youmatter.encoder;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.ItemStackHandler;
import realmayus.youmatter.ModContent;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.items.ThumbdriveItem;


public class EncoderScreen extends AbstractContainerScreen<EncoderMenu> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private EncoderBlockEntity encoder;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/encoder.png");

    public EncoderScreen(EncoderMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.encoder = container.encoder;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int xAxis = (mouseX - (width - WIDTH) / 2);
        int yAxis = (mouseY - (height - HEIGHT) / 2);

        if(xAxis >= 141 && xAxis <= 156 && yAxis >= 37 && yAxis <= 57) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.energy.title")), Component.literal(I18n.get("youmatter.gui.energy.description", encoder.getEnergy()))));
        }
        if (xAxis >= 16 && xAxis <= 32 && yAxis >= 59 && yAxis <= 75) {
            if (encoder.inventory.isPresent()) {
                ItemStackHandler inventory = encoder.inventory.resolve().get();
                if (inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem) {
                    CompoundTag nbt = inventory.getStackInSlot(1).getTag();
                    if (nbt != null) {
                        ListTag list = nbt.getList("stored_items", Tag.TAG_STRING);
                        if (list.size() >= 8) {
                            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.warning.encoder2"))));
                        }
                    }

                    return; //only dfraw the warning tooltip if the inventory is not present, or there is no thumbdrive inserted
                }
            }

            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.warning.encoder1"))));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawEnergyBolt(guiGraphics, encoder.getEnergy());
        drawProgressDisplayChain(guiGraphics, encoder.getProgress());

        encoder.inventory.ifPresent(inventory -> {
            if (!(inventory.getStackInSlot(1).getItem() instanceof ThumbdriveItem)) {
                guiGraphics.blit(GUI, 16, 59, 176, 66, 16, 16);
            } else {
                CompoundTag nbt = inventory.getStackInSlot(1).getTag();
                if (nbt != null) {
                    ListTag list = nbt.getList("stored_items", Tag.TAG_STRING);
                    if (list.size() >= 8) {
                        guiGraphics.blit(GUI, 16, 59, 176, 66, 16, 16);
                    }
                }
            }
        });
        guiGraphics.drawString(font, I18n.get(ModContent.ENCODER_BLOCK.get().getDescriptionId()), 8, 6, 0x404040, false);
    }

    private void drawProgressDisplayChain(GuiGraphics guiGraphics, int progress) {
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

        guiGraphics.blit(GUI, 22, 41, 176, 41, Math.round((arrow1 / 100.0f) * 18), 12);
        guiGraphics.blit(GUI, 47, 40, 176, 53, 7, Math.round((lock / 100.0f) * 13));
        guiGraphics.blit(GUI, 61, 41, 176, 41, Math.round((arrow2 / 100.0f) * 18), 12);

    }

    private void drawEnergyBolt(GuiGraphics guiGraphics, int energy) {
        if(energy == 0) {
            guiGraphics.blit(GUI, 141, 36, 176, 21, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            guiGraphics.blit(GUI, 141, 36, 176, 0, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.
        }
    }

    private void drawTooltip(GuiGraphics guiGraphics, int x, int y, List<Component> tooltips) {
        guiGraphics.renderComponentTooltip(font, tooltips, x, y);
    }
}
