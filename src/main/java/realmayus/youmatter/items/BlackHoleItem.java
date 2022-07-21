package realmayus.youmatter.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import realmayus.youmatter.YouMatter;

public class BlackHoleItem extends Item {

    public BlackHoleItem() {
        super(new Properties().stacksTo(1).tab(YouMatter.ITEM_GROUP));
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(new TextComponent(I18n.get("youmatter.tooltip.craftingItemEndCities")));
    }
}
