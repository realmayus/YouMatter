package org.realverse.youmatter.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.realverse.youmatter.YMConfig;

public class GeneralUtils {
    public static int getUMatterAmountForItem(Item item) {
        if(YMConfig.get().getOverride(RegistryUtil.getRegistryName(item).toString()) != null) {
            return Integer.parseInt((String)YMConfig.get().getOverride(RegistryUtil.getRegistryName(item).toString())[1]);
        } else {
            return YMConfig.get().defaultAmount;
        }
    }

    public static int getUMatterAmountForItem(ItemStack[] items) {
        for(ItemStack item : items) {
            if (hasCustomUMatterValue(item)) {
                return getUMatterAmountForItem(item.getItem());
            }
        }
        return YMConfig.get().defaultAmount;
    }

    public static boolean hasCustomUMatterValue(ItemStack item) {
        return YMConfig.get().getOverride(RegistryUtil.getRegistryName(item.getItem()).toString()) != null;
    }

    public static boolean hasCustomUMatterValue(ItemStack[] items) {
        for(ItemStack is : items) {
            if(YMConfig.get().getOverride(RegistryUtil.getRegistryName(is.getItem()).toString()) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean canAddItemToSlot(ItemStack slotStack, ItemStack givenStack, boolean stackSizeMatters) {
        boolean flag = slotStack.isEmpty();
        if (!flag && ItemStack.isSameItem(givenStack, slotStack)) {
            return slotStack.getCount() + (stackSizeMatters ? 0 : givenStack.getCount()) <= givenStack.getMaxStackSize();
        } else {
            return flag;
        }
    }
}
