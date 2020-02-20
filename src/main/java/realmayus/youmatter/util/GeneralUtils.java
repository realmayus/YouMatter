package realmayus.youmatter.util;

import net.minecraft.item.Item;
import realmayus.youmatter.YMConfig;

public class GeneralUtils {

    public static int getUMatterAmountForItem(Item item) {
        if(YMConfig.CONFIG.getOverride(item.getRegistryName().toString()) != null) {
            return Integer.parseInt((String)YMConfig.CONFIG.getOverride(item.getRegistryName().toString())[1]);
        } else {
            return YMConfig.CONFIG.defaultAmount.get();
        }
    }

}
