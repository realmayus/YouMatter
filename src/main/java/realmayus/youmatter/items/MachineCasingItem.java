package realmayus.youmatter.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.List;

public class MachineCasingItem extends Item {
    public MachineCasingItem() {
        setCreativeTab(YouMatter.creativeTab);
        setTranslationKey(YouMatter.MODID + ".machine_casing");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("youmatter.tooltip.craftingItem"));
    }
}
