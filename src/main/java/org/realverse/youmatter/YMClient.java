package org.realverse.youmatter;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

@Mod(
        value = YouMatter.MODID,
        dist = Dist.CLIENT
)
public class YMClient {
    public YMClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModContent.SCANNER_MENU.get(), ScannerScreen::new);
        event.register(ModContent.ENCODER_MENU.get(), EncoderScreen::new);
        event.register(ModContent.REPLICATOR_MENU.get(), ReplicatorScreen::new);
        event.register(ModContent.CREATOR_MENU.get(), CreatorScreen::new);
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        registerFluidType(event, ModContent.STABILIZER_TYPE.get(), "stabilizer");
        registerFluidType(event, ModContent.UMATTER_TYPE.get(), "umatter");
    }

    private static void registerFluidType(RegisterClientExtensionsEvent event, FluidType fluidType, final String fluidName) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private final ResourceLocation STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath("youmatter", "block/" + fluidName + "_still");
            private final ResourceLocation FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath("youmatter", "block/" + fluidName + "_flow");

            public @NotNull ResourceLocation getStillTexture() {
                return this.STILL_TEXTURE;
            }

            public @NotNull ResourceLocation getFlowingTexture() {
                return this.FLOWING_TEXTURE;
            }
        }, fluidType);
    }
}