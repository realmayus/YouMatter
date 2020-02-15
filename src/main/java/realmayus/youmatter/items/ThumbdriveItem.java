package realmayus.youmatter.items;

//import net.minecraft.client.resources.I18n;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
//import net.minecraft.nbt.NBTTagString;
//import net.minecraft.util.EnumActionResult;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.TextComponentString;
//import net.minecraft.world.World;
//import net.minecraftforge.common.util.Constants;
//import realmayus.youmatter.YouMatter;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.UUID;
//
//public class ThumbdriveItem extends Item {
//
//    public ThumbdriveItem() {
//         maxStackSize = 1;
//         setCreativeTab(YouMatter.creativeTab);
//         setTranslationKey(YouMatter.MODID + ".thumb_drive");
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        if(stack.hasTagCompound()) {
//            if(stack.getTagCompound() != null) {
//                if (stack.getTagCompound().hasKey("stored_items")) {
//                    tooltip.add(I18n.format("youmatter.tooltip.dataStored"));
//                    tooltip.add(I18n.format("youmatter.tooltip.remainingSpace", stack.getTagCompound().getTagList("stored_items", Constants.NBT.TAG_STRING).tagCount(), 8));
//                } else {
//                    tooltip.add(I18n.format("youmatter.tooltip.noDataStored"));
//                }
//            }
//        }
//    }
//}
