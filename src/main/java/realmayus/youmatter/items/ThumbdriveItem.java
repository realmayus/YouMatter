package realmayus.youmatter.items;


import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.List;

public class ThumbdriveItem extends Item {

    public ThumbdriveItem() {
        super(new Item.Properties().stacksTo(1).tab(YouMatter.ITEM_GROUP));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(stack.hasTag()) {
            if(stack.getTag() != null) {
                if (stack.getTag().contains("stored_items", Constants.NBT.TAG_LIST)) {
                    tooltip.add(new TextComponent(I18n.get("youmatter.tooltip.dataStored")));
                    tooltip.add(new TextComponent(I18n.get("youmatter.tooltip.remainingSpace", stack.getTag().getList("stored_items", Constants.NBT.TAG_STRING).size(), 8)));
                } else {
                    tooltip.add(new TextComponent(I18n.get("youmatter.tooltip.noDataStored")));
                }
            }
        }
    }

}
