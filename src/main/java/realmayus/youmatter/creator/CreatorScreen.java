package realmayus.youmatter.creator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import realmayus.youmatter.ObjectHolders;
import realmayus.youmatter.YouMatter;
import realmayus.youmatter.network.PacketChangeSettingsCreatorServer;
import realmayus.youmatter.network.PacketHandler;

import java.util.Arrays;
import java.util.List;

public class CreatorScreen extends ContainerScreen<CreatorContainer> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 168;

    private CreatorTile te;

    private static final ResourceLocation GUI = new ResourceLocation(YouMatter.MODID, "textures/gui/creator.png");

    public CreatorScreen(CreatorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.te = container.te;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        //Setting color to white because JEI is bae (gui would be yellow)
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI);

        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, WIDTH, HEIGHT);

        drawFluidTank(matrixStack, 89, 22, te.getUTank());
        drawFluidTank(matrixStack,31, 22, te.getSTank());
    }

    private void drawActiveIcon(MatrixStack matrixStack, boolean isActive) {
        this.minecraft.getTextureManager().bind(GUI);

        if(isActive) {
            this.blit(matrixStack, 154, 13, 176, 24, 8, 9);

        } else {
            this.blit(matrixStack, 154, 13, 176, 15, 8, 9);
        }
    }


    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawEnergyBolt(matrixStack, te.getClientEnergy());
        drawActiveIcon(matrixStack, te.isActivatedClient());

        this.minecraft.getTextureManager().bind(GUI);

        font.draw(matrixStack, I18n.get(ObjectHolders.CREATOR_BLOCK.getDescriptionId()), 8, 6, 0x404040);
    }

    private void drawEnergyBolt(MatrixStack matrixStack, int energy) {
        this.minecraft.getTextureManager().bind(GUI);

        if(energy == 0) {
            this.blit(matrixStack, 150, 58, 176, 114, 15, 20);
        } else {
            double percentage = energy * 100.0F / 1000000;  // i know this is dumb
            float percentagef = (float) percentage / 100; // but it works.
            this.blit(matrixStack, 150, 58, 176, 93, 15, Math.round(20 * percentagef)); // it's not really intended that the bolt fills from the top but it looks cool tbh.

        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        //Render the dark background
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        //Render any tooltips
        this.renderTooltip(matrixStack, mouseX, mouseY);

        int xAxis = (mouseX - (width - imageWidth) / 2);
        int yAxis = (mouseY - (height - imageHeight) / 2);

        if(xAxis >= 31 && xAxis <= 44 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new StringTextComponent(I18n.get("youmatter.gui.stabilizer.title")), new StringTextComponent(I18n.get("youmatter.gui.stabilizer.description", te.getSTank().getFluidAmount()))));
        }
        if(xAxis >= 89 && xAxis <= 102 && yAxis >= 20 && yAxis <= 75) {
            drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new StringTextComponent(I18n.get("youmatter.gui.umatter.title")), new StringTextComponent(I18n.get("youmatter.gui.umatter.description", te.getUTank().getFluidAmount()))));
        }
        if(xAxis >= 150 && xAxis <= 164 && yAxis >= 57 && yAxis <= 77) {
            drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new StringTextComponent(I18n.get("youmatter.gui.energy.title")), new StringTextComponent(I18n.get("youmatter.gui.energy.description", te.getClientEnergy()))));
        }
        if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
            drawTooltip(matrixStack, mouseX, mouseY, Arrays.asList(new StringTextComponent(te.isActivatedClient() ? I18n.get("youmatter.gui.active") : I18n.get("youmatter.gui.paused")), new StringTextComponent(I18n.get("youmatter.gui.clicktochange"))));
        }
    }


    private void drawTooltip(MatrixStack matrixStack, int x, int y, List<ITextComponent> tooltips) {
        renderComponentTooltip(matrixStack, tooltips, x, y);
    }


    //both drawFluid and drawFluidTank is courtesy of DarkGuardsMan and was modified to suit my needs. Go check him out: https://github.com/BuiltBrokenModding/Atomic-Science | MIT License |  Copyright (c) 2018 Built Broken Modding License: https://opensource.org/licenses/MIT
    private void drawFluid(MatrixStack matrixStack, int x, int y, int line, int col, int width, int drawSize, FluidStack fluidStack)
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

            this.getMinecraft().getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);

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
                blit(matrixStack, x + col, y + line + 58 - renderY - start, 1000, width, textureSize - (textureSize - renderY), this.minecraft.getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(fluidIcon));


                start = start + textureSize;
            }
        }
    }

    private void drawFluidTank(MatrixStack matrixStack, int x, int y, IFluidTank tank) {

        //Get data
        final float scale = tank.getFluidAmount() / (float) tank.getCapacity();
        final FluidStack fluidStack = tank.getFluid();

        //Reset color
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);


        //Draw fluid
        int meterHeight = 55;
        if (fluidStack != null)
        {
            this.drawFluid(matrixStack, this.leftPos + x -1, this.topPos + y, -3, 1, 14, (int) ((meterHeight - 1) * scale), fluidStack);
        }

        //Draw lines
        this.minecraft.getTextureManager().bind(GUI);
        int meterWidth = 14;
        this.blit(matrixStack, this.leftPos + x, this.topPos + y, 176, 35, meterWidth, meterHeight);

        //Reset color
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            double xAxis = (mouseX - (width - imageWidth) / 2);
            double yAxis = (mouseY - (height - imageHeight) / 2);
            if(xAxis >= 148 && xAxis <= 167 && yAxis >= 7 && yAxis <= 27) {
                //Playing Click sound
                Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                //Sending packet to server
                PacketHandler.INSTANCE.sendToServer(new PacketChangeSettingsCreatorServer(!te.isActivatedClient()));
            }
        }
        return true;
    }
}
