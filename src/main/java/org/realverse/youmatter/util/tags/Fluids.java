package org.realverse.youmatter.util.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.realverse.youmatter.YouMatter;


public class Fluids {
    public static final TagKey<Fluid> STABILIZER = tag("stabilizer");

    private static TagKey<Fluid> tag(String name) {
        return FluidTags.create(ResourceLocation.fromNamespaceAndPath(YouMatter.MODID, name));
    }
}

