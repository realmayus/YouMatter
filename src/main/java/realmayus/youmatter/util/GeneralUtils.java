package realmayus.youmatter.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import realmayus.youmatter.YMConfig;

public class GeneralUtils {
    public static int getUMatterAmountForItem(Item item) {
        if(YMConfig.overrides.containsKey(item.getRegistryName().toString())) {
            return YMConfig.overrides.getOrDefault(item.getRegistryName().toString(), YMConfig.uMatterPerItem);
        } else {
            return YMConfig.uMatterPerItem;
        }
    }


    public static boolean canAddItemToSlot(ItemStack slotStack, ItemStack givenStack, boolean stackSizeMatters) {
        boolean flag = slotStack.isEmpty();
        if (!flag && givenStack.isItemEqual(slotStack) /*&& ItemStack.areItemStackTagsEqual(slotStack, givenStack)*/) {
            return slotStack.getCount() + (stackSizeMatters ? 0 : givenStack.getCount()) <= givenStack.getMaxStackSize();
        } else {
            return flag;
        }
    }
}
