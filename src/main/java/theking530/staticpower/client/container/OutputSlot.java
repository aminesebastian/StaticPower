package theking530.staticpower.client.container;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.api.container.StaticPowerContainerSlot;

public class OutputSlot extends StaticPowerContainerSlot {

	public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
		return false;	          
    }

}
