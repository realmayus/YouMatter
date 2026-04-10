package org.realverse.youmatter.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class RegistryUtil {
	public static final ResourceLocation getRegistryName(Item item) {
		return BuiltInRegistries.ITEM.getKey(item);
	}

	public static final ResourceLocation getRegistryName(Fluid fluid) {
		return BuiltInRegistries.FLUID.getKey(fluid);
	}
}
