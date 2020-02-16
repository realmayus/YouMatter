package realmayus.youmatter.items;


import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.List;

public class ThumbdriveItem extends Item {

    public ThumbdriveItem() {
        super(new Item.Properties().maxStackSize(1).group(YouMatter.ITEM_GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag()) {
            if(stack.getTag() != null) {
                if (stack.getTag().contains("stored_items", Constants.NBT.TAG_LIST)) {
                    tooltip.add(new StringTextComponent(I18n.format("youmatter.tooltip.dataStored")));
                    tooltip.add(new StringTextComponent(I18n.format("youmatter.tooltip.remainingSpace", stack.getTag().getList("stored_items", Constants.NBT.TAG_STRING).size(), 8)));
                } else {
                    tooltip.add(new StringTextComponent(I18n.format("youmatter.tooltip.noDataStored")));
                }
            }
        }
    }

}
