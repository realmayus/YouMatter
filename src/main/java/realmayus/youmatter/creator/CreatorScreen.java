package realmayus.youmatter.creator;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.network.PacketChangeSettingsCreatorServer;
import realmayus.youmatter.network.PacketHandler;

public class CreatorScreen extends AbstractContainerScreen<CreatorMenu> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private CreatorBlockEntity creator;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/creator.png");

    public CreatorScreen(CreatorMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.creator = container.creator;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem._setShaderTexture(0, GUI);

        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(poseStack, relX, relY, 0, 0, WIDTH, HEIGHT);

        drawFluidTank(poseStack, 89, 22, creator.getUTank());
        drawFluidTank(poseStack,31, 22, creator.getSTank());
    }

    private void drawActiveIcon(PoseStack poseStack, boolean isActive) {
        RenderSystem._setShaderTexture(0, GUI);

        if(isActive) {
            this.blit(poseStack, 154, 13, 176, 24, 8, 9);

        } else {
            this.blit(poseStack, 154, 13, 176, 15, 8, 9);
        }
    }


    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        drawEnergyBolt(poseStack, creator.getEnergy());
        drawActiveIcon(poseStack, creator.isActivated());

        RenderSystem._setShaderTexture(0, GUI);

        font.draw(poseStack, I18n.get(ObjectHolders.CREATOR_BLOCK.getDescriptionId()), 8, 6, 0x404040);
    }

    private void drawEnergyBolt(PoseStack poseStack, int energy) {
        RenderSystem._setShaderTexture(0, GUI);

        if(energy == 0) {
            this.blit(poseStack, 150, 58, 176, 114, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            this.blit(poseStack, 150, 58, 176, 93, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        //Render the dark background
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        //Render any tooltips
        this.renderTooltip(poseStack, mouseX, mouseY);

        int xAxis = (mouseX - (width - imageWidth) / 2);
        int yAxis = (mouseY - (height - imageHeight) / 2);

        if(xAxis >= 31 && xAxis <= 44 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(poseStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.gui.stabilizer.title")), new TextComponent(I18n.get("youmatter.gui.stabilizer.description", creator.getSTank().getFluidAmount()))));
        }
        if(xAxis >= 89 && xAxis <= 102 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(poseStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.gui.umatter.title")), new TextComponent(I18n.get("youmatter.gui.umatter.description", creator.getUTank().getFluidAmount()))));
        }
        if(xAxis >= 150 && xAxis <= 164 && yAxis >= 57 && yAxis <= 77) {
            drawTooltip(poseStack, mouseX, mouseY, Arrays.asList(new TextComponent(I18n.get("youmatter.gui.energy.title")), new TextComponent(I18n.get("youmatter.gui.energy.description", creator.getEnergy()))));
        }
        if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
            drawTooltip(poseStack, mouseX, mouseY, Arrays.asList(new TextComponent(creator.isActivated() ? I18n.get("youmatter.gui.active") : I18n.get("youmatter.gui.paused")), new TextComponent(I18n.get("youmatter.gui.clicktochange"))));
        }
    }


    private void drawTooltip(PoseStack poseStack, int x, int y, List<Component> tooltips) {
        renderComponentTooltip(poseStack, tooltips, x, y);
    }


    //both drawFluid and drawFluidTank is courtesy of DarkGuardsMan and was modified to suit my needs. Go check him out: https://github.com/BuiltBrokenModding/Atomic-Science | MIT License |  Copyright (c) 2018 Built Broken Modding License: https://opensource.org/licenses/MIT
    private void drawFluid(PoseStack poseStack, int x, int y, int line, int col, int width, int drawSize, FluidStack fluidStack)
    {
        if (fluidStack != null && fluidStack.getFluid() != null && !fluidStack.isEmpty())
        {
            drawSize -= 1;
            ResourceLocation fluidIcon;
            Fluid fluid = fluidStack.getFluid();

            ResourceLocation waterSprite = Fluids.WATER.getAttributes().getStillTexture(new FluidStack(Fluids.WATER, 1000));

            if (fluid instanceof FlowingFluid) {
                if (fluid.getAttributes().getStillTexture(fluidStack) != null) {
                    fluidIcon = fluid.getAttributes().getStillTexture(fluidStack);
                } else if (fluid.getAttributes().getFlowingTexture(fluidStack) != null) {
                    fluidIcon = fluid.getAttributes().getFlowingTexture(fluidStack);
                } else {
                    fluidIcon = waterSprite;
                }
            } else {
                fluidIcon = waterSprite;
            }

            //Bind fluid texture

            RenderSystem._setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

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

                //TODO?
                blit(poseStack, x + col, y + line + 58 - renderY - start, 1000, width, textureSize - (textureSize - renderY), this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidIcon));


                start = start + textureSize;
            }
        }
    }

    private void drawFluidTank(PoseStack poseStack, int x, int y, IFluidTank tank) {

        //Get data
        final float scale = tank.getFluidAmount() / (float) tank.getCapacity();
        final FluidStack fluidStack = tank.getFluid();

        //Reset color
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


        //Draw fluid
        int meterHeight = 55;
        if (fluidStack != null)
        {
            this.drawFluid(poseStack, this.leftPos + x -1, this.topPos + y, -3, 1, 14, (int) ((meterHeight - 1) * scale), fluidStack);
        }

        //Draw lines
        RenderSystem._setShaderTexture(0, GUI);
        int meterWidth = 14;
        this.blit(poseStack, this.leftPos + x, this.topPos + y, 176, 35, meterWidth, meterHeight);

        //Reset color
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            double xAxis = (mouseX - (width - imageWidth) / 2);
            double yAxis = (mouseY - (height - imageHeight) / 2);
            if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketChangeSettingsCreatorServer(!creator.isActivated()));
            }
        }
        return true;
    }
}
