package realmayus.youmatter.items;

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
        nbt.setTag("MyStringList", list);

        player.sendMessage(new TextComponentString("Loaded NBT Data."));
        player.sendMessage(new TextComponentString(stack.getTagCompound().toString()));

        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
}
