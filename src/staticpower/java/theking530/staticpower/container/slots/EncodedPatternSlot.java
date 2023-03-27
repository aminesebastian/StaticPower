package theking530.staticpower.container.slots;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;

public class EncodedPatternSlot extends StaticPowerContainerSlot {
	public EncodedPatternSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	public EncodedPatternSlot(IItemHandler itemHandler, ItemStack preview, int index, int xPosition, int yPosition) {
		super(preview, 0.3f, itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return stack.getItem() == ModItems.PatternCard.get();
	}
}
