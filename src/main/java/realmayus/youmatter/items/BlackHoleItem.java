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

public class BlackHoleItem extends Item {

    public BlackHoleItem() {
        super(new Properties().maxStackSize(1).group(YouMatter.ITEM_GROUP));
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(I18n.format("youmatter.tooltip.craftingItemEndCities")));
    }
}
