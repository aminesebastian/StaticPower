package theking530.staticpower.client.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.items.itemfilter.ItemFilter;

public class FilterItemSlot extends StaticPowerContainerSlot {

	public FilterItemSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(new ItemStack(ModItems.BasicFilter), 0.3f, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemFilter;
	}
}
