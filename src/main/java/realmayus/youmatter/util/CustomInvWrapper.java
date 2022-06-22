package realmayus.youmatter.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class CustomInvWrapper extends CombinedInvWrapper {

    public CustomInvWrapper(IItemHandlerModifiable... itemHandler) {
        super(itemHandler);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot == 2) {
            return stack;
        } else {
            return super.insertItem(slot, stack, simulate);
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 2) {
            return ItemStack.EMPTY;
        } else {
            return super.extractItem(slot, amount, simulate);
        }
    }
}