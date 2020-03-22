package realmayus.youmatter.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import realmayus.youmatter.YMConfig;

import java.util.ArrayList;
import java.util.List;

public class GeneralUtils {

    /**
     * Sorts all available recipes in lists of recipes that consist of lists of required items that consist of a list of available variants.
     * @param manager a RecipeManager object.
     * @return every recipe that is available in a handy list.
     */
    public static List<IRecipe<?>> getMatchingRecipes(RecipeManager manager, ItemStack is) { // List of Recipes > List of Required Items For that recipe > List of allowed ItemStacks as an ingredient (see OreDict)
        List<IRecipe<?>> returnValue = new ArrayList<>();
        for(IRecipe<?> recipe : manager.getRecipes()) {
            if(recipe.getRecipeOutput().isItemEqual(is)) {
                returnValue.add(recipe);
            }
        }
        return returnValue;
    }

    public static int getUMatterAmountForItem(Item item) {
        if(YMConfig.CONFIG.getOverride(item.getRegistryName().toString()) != null) {
            return Integer.parseInt((String)YMConfig.CONFIG.getOverride(item.getRegistryName().toString())[1]);
        } else {
            return YMConfig.CONFIG.defaultAmount.get();
        }
    }

    public static int getUMatterAmountForItem(ItemStack[] items) {
        for(ItemStack item : items) {
            if (hasCustomUMatterValue(item)) {
                return getUMatterAmountForItem(item.getItem());
            }
        }
        return YMConfig.CONFIG.defaultAmount.get();
    }

    public static boolean hasCustomUMatterValue(ItemStack item) {
        return YMConfig.CONFIG.getOverride(item.getItem().getRegistryName().toString()) != null;
    }

    public static boolean hasCustomUMatterValue(ItemStack[] items) {
        for(ItemStack is : items) {
            if(YMConfig.CONFIG.getOverride(is.getItem().getRegistryName().toString()) != null) {
                return true;
            }
        }
        return false;
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
