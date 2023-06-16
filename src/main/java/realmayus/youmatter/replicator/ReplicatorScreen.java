package realmayus.youmatter.replicator;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import realmayus.youmatter.ModContent;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.network.PacketChangeSettingsReplicatorServer;
import realmayus.youmatter.network.PacketHandler;
import realmayus.youmatter.network.PacketShowNext;
import realmayus.youmatter.network.PacketShowPrevious;
import realmayus.youmatter.util.DisplaySlot;
import realmayus.youmatter.util.GeneralUtils;


public class ReplicatorScreen extends AbstractContainerScreen<ReplicatorMenu> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private ReplicatorBlockEntity replicator;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/replicator.png");

    public ReplicatorScreen(ReplicatorMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.replicator = container.replicator;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, WIDTH, HEIGHT);

        drawFluidTank(guiGraphics, 26, 20, replicator.getTank());

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawEnergyBolt(guiGraphics, replicator.getEnergy());
        drawActiveIcon(guiGraphics, replicator.isActive());
        drawModeIcon(guiGraphics, replicator.isCurrentMode());
        drawProgressArrow(guiGraphics, replicator.getProgress());

        guiGraphics.drawString(font, I18n.get(ModContent.REPLICATOR_BLOCK.get().getDescriptionId()), 8, 6, 0x404040, false);
    }

    private void drawEnergyBolt(GuiGraphics guiGraphics, int energy) {
        if(replicator.getEnergy() == 0) {
            guiGraphics.blit(GUI, 127, 58, 176, 114, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float)percentage / 100; // but it works.
            guiGraphics.blit(GUI, 127, 58, 176, 93, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }


    private void drawProgressArrow(GuiGraphics guiGraphics, int progress) {
        guiGraphics.blit(GUI, 91, 38, 176, 134, 11, Math.round((progress / 100.0f) * 19));
    }

    private void drawActiveIcon(GuiGraphics guiGraphics, boolean isActive) {
        if(isActive) {
            guiGraphics.blit(GUI, 154, 12, 176, 24, 8, 9);
        } else {
            guiGraphics.blit(GUI, 154, 12, 184, 24, 8, 9);
        }
    }

    private void drawModeIcon(GuiGraphics guiGraphics, boolean mode) {
        if (mode){
            //loop
            guiGraphics.blit(GUI, 152, 34, 176, 11, 13,13);
        } else {
            guiGraphics.blit(GUI, 151, 35, 176, 0, 13, 11);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //Render the dark background

        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);



        //Render any tooltips
        renderTooltip(guiGraphics, mouseX, mouseY);

        int xAxis = (mouseX - (width - imageWidth) / 2);
        int yAxis = (mouseY - (height - imageHeight) / 2);

        if(xAxis >= 26 && xAxis <= 39 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.umatter.title")), Component.literal(I18n.get("youmatter.gui.umatter.description", replicator.getTank().getFluid().getAmount()))));
        }

        if(xAxis >= 127 && xAxis <= 142 && yAxis >= 59 && yAxis <= 79) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(I18n.get("youmatter.gui.energy.title")), Component.literal(I18n.get("youmatter.gui.energy.description", replicator.getEnergy()))));
        }

        if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(replicator.isActive() ? I18n.get("youmatter.gui.active") : I18n.get("youmatter.gui.paused")), Component.literal(I18n.get("youmatter.gui.clicktochange"))));
        }

        if(xAxis >= 148 && xAxis <= 167 && yAxis >= 31 && yAxis <= 51) {
            drawTooltip(guiGraphics, mouseX, mouseY, Arrays.asList(Component.literal(replicator.isCurrentMode() ? I18n.get("youmatter.gui.performInfiniteRuns") : I18n.get("youmatter.gui.performSingleRun")), Component.literal(I18n.get("youmatter.gui.clicktochange"))));
        }
    }

    private void drawTooltip(GuiGraphics guiGraphics, int x, int y, List<Component> tooltips) {
        guiGraphics.renderComponentTooltip(font, tooltips, x, y);
    }

    @Override
    public List<Component> getTooltipFromContainerItem(ItemStack givenItem) {
        if (hoveredSlot instanceof DisplaySlot) {
            if(ItemStack.isSameItem(givenItem, hoveredSlot.getItem())) {
                List<Component> existingTooltips =  super.getTooltipFromContainerItem(givenItem);
                existingTooltips.add(Component.literal(""));
                existingTooltips.add(Component.literal(I18n.get("gui.youmatter.requiredAmount", GeneralUtils.getUMatterAmountForItem(givenItem.getItem()))));
                return existingTooltips;
            }
        }
        return super.getTooltipFromContainerItem(givenItem);
    }

    //both drawFluid and drawFluidTank is courtesy of DarkGuardsMan and was modified to suit my needs. Go check him out: https://github.com/BuiltBrokenModding/Atomic-Science | MIT License |  Copyright (c) 2018 Built Broken Modding License: https://opensource.org/licenses/MIT
    private void drawFluid(GuiGraphics guiGraphics, int x, int y, int line, int col, int width, int drawSize, FluidStack fluidStack)
    {
        if (fluidStack != null && fluidStack.getFluid() != null && !fluidStack.isEmpty())
        {
            drawSize -= 1;
            ResourceLocation fluidIcon;
            Fluid fluid = fluidStack.getFluid();

            ResourceLocation waterSprite = IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture(new FluidStack(Fluids.WATER, 1000));

            if (fluid instanceof FlowingFluid) {
                if (IClientFluidTypeExtensions.of(fluid).getStillTexture(fluidStack) != null) {
                    fluidIcon = IClientFluidTypeExtensions.of(fluid).getStillTexture(fluidStack);
                } else if (IClientFluidTypeExtensions.of(fluid).getFlowingTexture(fluidStack) != null) {
                    fluidIcon = IClientFluidTypeExtensions.of(fluid).getFlowingTexture(fluidStack);
                } else {
                    fluidIcon = waterSprite;
                }
            } else {
                fluidIcon = waterSprite;
            }

            final int textureSize = 16;
            int start = 0;
            int renderY;
            while (drawSize != 0) {
                if (drawSize > textureSize) {
                    renderY = textureSize;
                    drawSize -= textureSize;
                } else {
                    renderY = drawSize;
                    drawSize = 0;
                }

                guiGraphics.blit(x + col, y + line + 58 - renderY - start, 1000, width, textureSize - (textureSize - renderY), this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidIcon));
                start = start + textureSize;
            }
        }
    }

    private void drawFluidTank(GuiGraphics guiGraphics, int x, int y, IFluidTank tank) {

        //Get data
        final float scale = tank.getFluidAmount() / (float) tank.getCapacity();
        final FluidStack fluidStack = tank.getFluid();


        //Draw fluid
        int meterHeight = 55;
        if (fluidStack != null)
        {
            this.drawFluid(guiGraphics, this.leftPos + x -1, this.topPos + y, -3, 1, 14, (int) ((meterHeight - 1) * scale), fluidStack);
        }

        //Draw lines
        int meterWidth = 14;
        guiGraphics.blit(GUI, this.leftPos + x, this.topPos + y, 176, 35, meterWidth, meterHeight);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            double xAxis = (mouseX - (width - imageWidth) / 2);
            double yAxis = (mouseY - (height - imageHeight) / 2);
            if(xAxis >= 80 && xAxis <= 85 && yAxis >= 21 && yAxis <= 31) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                replicator.renderPrevious();
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketShowPrevious());
            } else if(xAxis >= 108 && xAxis <= 113 && yAxis >= 21 && yAxis <= 31) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                replicator.renderNext();
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketShowNext() );
            } else if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                replicator.setActive(!replicator.isActive());
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketChangeSettingsReplicatorServer(replicator.isActive(), replicator.isCurrentMode()) );
            } else if(xAxis >= 148 && xAxis <= 167 && yAxis >= 31 && yAxis <= 51) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                replicator.setCurrentMode(!replicator.isCurrentMode());
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketChangeSettingsReplicatorServer(replicator.isActive(), replicator.isCurrentMode()) );
            }
        }
        return true;
    }
}