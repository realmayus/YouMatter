package realmayus.youmatter.items;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.level.Level;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class BlackHoleItem extends Item {

    public BlackHoleItem() {
        super(new Properties().stacksTo(1).tab(YouMatter.ITEM_GROUP));
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TextComponent(I18n.get("youmatter.tooltip.craftingItemEndCities")));
    }
}
