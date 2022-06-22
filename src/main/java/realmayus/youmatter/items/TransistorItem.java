package realmayus.youmatter.items;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class TransistorItem extends Item {

    public TransistorItem() {
        super(new Properties().tab(YouMatter.ITEM_GROUP));
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TextComponent(I18n.get("youmatter.tooltip.craftingItem")));
    }
}
