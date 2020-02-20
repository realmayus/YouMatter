package realmayus.youmatter.util;

import net.minecraft.item.Item;
import realmayus.youmatter.YMConfig;

public class GeneralUtils {
    public static int getUMatterAmountForItem(Item item) {
        if(YMConfig.overrides.containsKey(item.getRegistryName().toString())) {
            return YMConfig.overrides.getOrDefault(item.getRegistryName().toString(), YMConfig.uMatterPerItem);
        } else {
            return YMConfig.uMatterPerItem;
        }
    }
}
