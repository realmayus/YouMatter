package realmayus.youmatter;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(value= Side.CLIENT)
public class ClientRegistryHandler {

    /**
     * Will be called by Forge automatically when it's time.
     * Stolen from Cadiboo https://gist.github.com/Cadiboo/3f5cdb785affc069af2fa5fdf2d70358
     */
    @SubscribeEvent
    public static void onRegisterModelsEvent(@Nonnull final ModelRegistryEvent event) {
        ModelLoader.setCustomStateMapper(ModBlocks.UMATTER_BLOCK, new StateMap.Builder().ignore(ModBlocks.UMATTER_BLOCK.LEVEL).build());
        ModelLoader.setCustomStateMapper(ModBlocks.STABILIZER_BLOCK, new StateMap.Builder().ignore(ModBlocks.STABILIZER_BLOCK.LEVEL).build());

        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item.getRegistryName().getNamespace().equals(YouMatter.MODID))
                .forEach(item -> ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "normal")));
    }
}
