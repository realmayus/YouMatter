package realmayus.youmatter.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryUtil {
	public static final ResourceLocation getRegistryName(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}

	public static final ResourceLocation getRegistryName(Fluid fluid) {
		return ForgeRegistries.FLUIDS.getKey(fluid);
	}
}
