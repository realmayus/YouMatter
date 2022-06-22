package realmayus.youmatter.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class BlackHoleItem extends Item {

    public BlackHoleItem() {
        super(new Properties().stacksTo(1).tab(YouMatter.ITEM_GROUP));
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(I18n.get("youmatter.tooltip.craftingItemEndCities")));
    }
}
