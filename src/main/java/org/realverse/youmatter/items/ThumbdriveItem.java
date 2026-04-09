package org.realverse.youmatter.items;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ThumbdriveItem extends Item {
    public ThumbdriveItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if(stack.hasTag()) {
            if (stack.getTag().contains("stored_items", Tag.TAG_LIST)) {
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.dataStored")));
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.remainingSpace", stack.getTag().getList("stored_items", Tag.TAG_STRING).size(), 8)));
            } else {
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.noDataStored")));
            }
        }
    }
}
