package theking530.staticpower.client.container;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.IItemHandler;
import theking530.api.container.StaticPowerContainerSlot;

public class FilterSlot extends StaticPowerContainerSlot {

	public FilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(new ItemStack(Items.LAPIS_BLOCK), itemHandler, index, xPosition, yPosition);
	}
    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
		return false; // !stack.isEmpty() && stack.getItem() instanceof ItemFilter;	          
    }

}
