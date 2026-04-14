package org.realverse.youmatter;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.realverse.youmatter.creator.CreatorScreen;
import org.realverse.youmatter.creator.old.CreatorScreenOld;
import org.realverse.youmatter.encoder.EncoderScreen;
import org.realverse.youmatter.replicator.ReplicatorScreen;
import org.realverse.youmatter.scanner.ScannerScreen;

@Mod(
        value = YouMatter.MODID,
        dist = Dist.CLIENT
)
public class YMClient {
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModContent.SCANNER_MENU.get(), ScannerScreen::new);
        event.register(ModContent.ENCODER_MENU.get(), EncoderScreen::new);
        event.register(ModContent.REPLICATOR_MENU.get(), ReplicatorScreen::new);
        event.register(ModContent.CREATOR_MENU.get(), CreatorScreen::new);
        event.register(ModContent.CREATOR_MENU_OLD.get(), CreatorScreenOld::new);
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        registerFluidType(event, ModContent.STABILIZER_TYPE.get(), "stabilizer");
        registerFluidType(event, ModContent.UMATTER_TYPE.get(), "umatter");
    }

    private static void registerFluidType(RegisterClientExtensionsEvent event, FluidType fluidType, final String fluidName) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private final ResourceLocation STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "block/" + fluidName + "_still");
            private final ResourceLocation FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, "block/" + fluidName + "_flow");

            public @NotNull ResourceLocation getStillTexture() {
                return this.STILL_TEXTURE;
            }

            public @NotNull ResourceLocation getFlowingTexture() {
                return this.FLOWING_TEXTURE;
            }
        }, fluidType);
    }
}