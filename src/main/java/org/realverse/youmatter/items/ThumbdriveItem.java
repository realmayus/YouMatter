package org.realverse.youmatter.items;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;
import org.realverse.youmatter.YMConfig;

import java.util.List;

public class ThumbdriveItem extends Item {
    public ThumbdriveItem() {
        super(new Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    public ItemContainerContents getContents(ItemStack stack) {
        return stack.get(DataComponents.CONTAINER);
    }

    public int getMaxStorage() {
        return YMConfig.get().thumbDriveSlots;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemContainerContents contents = this.getContents(stack);
        int maxStorage = this.getMaxStorage();
        if(contents != null) {
            if (contents.getSlots() > 0) {
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.dataStored")));
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.remainingSpace", contents.getSlots(), maxStorage)));
            } else {
                tooltip.add(Component.literal(I18n.get("youmatter.tooltip.noDataStored")));
            }
        }
    }
}
