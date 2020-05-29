package theking530.staticpower.client.container;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.api.container.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.items.itemfilter.ItemFilter;

public class FilterSlot extends StaticPowerContainerSlot {

	public FilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(new ItemStack(ModItems.BasicFilter), itemHandler, index, xPosition, yPosition);
	}
    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemFilter;	          
    }

}
