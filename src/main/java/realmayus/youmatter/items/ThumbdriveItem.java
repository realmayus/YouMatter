package realmayus.youmatter.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import realmayus.youmatter.YouMatter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ThumbdriveItem extends Item {

    public ThumbdriveItem() {
         maxStackSize = 1;
         setCreativeTab(YouMatter.creativeTab);
         setTranslationKey(YouMatter.MODID + ".thumb_drive");
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagList list = new NBTTagList();


        list.appendTag(new NBTTagString("youmatter:replicator"));

        list.appendTag(new NBTTagString("youmatter:thumb_drive"));


        assert nbt != null;
        nbt.setTag("stored_items", list); //todo rename

        player.sendMessage(new TextComponentString("Loaded NBT Data."));

        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTagCompound()) {
            if(stack.getTagCompound() != null) {
                if (stack.getTagCompound().hasKey("stored_items")) {
                    tooltip.add("§5Data stored.");
                    tooltip.add("(" + stack.getTagCompound().getTagList("stored_items", 9).tagCount() + "/8 KB used)"); //todo fix (always shows 0/8KB used)e
                } else {
                    tooltip.add("§cNo Data stored.");
                }
            }
        }
    }
}
