package theking530.staticpower.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class OutputSlot extends StaticPowerContainerSlot {

	public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	public OutputSlot(IItemHandler itemHandler, Item item, int index, int xPosition, int yPosition) {
		super(new ItemStack(item), 0.3f, itemHandler, index, xPosition, yPosition);
	}

	public OutputSlot(IItemHandler itemHandler, ItemStack item, int index, int xPosition, int yPosition) {
		super(item, 0.3f, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return false;
	}

}
