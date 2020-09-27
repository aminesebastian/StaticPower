package theking530.staticpower.container.slots;

import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class NoCountRenderSlot extends Slot {
	private Slot encapsulatedSlot;

	public NoCountRenderSlot(Slot slot) {
		super(slot.inventory, slot.getSlotIndex(), slot.xPos, slot.yPos);
		encapsulatedSlot = slot;
	}

	@Override
	public ItemStack getStack() {
		// Get the actual stack.
		ItemStack parent = encapsulatedSlot.getStack();
		// If its empty, just return it.
		if (parent.isEmpty()) {
			return parent;
		}

		// Otherwise, return a copy with a stack size of one.
		parent = parent.copy();
		parent.setCount(1);

		return parent;
	}
}
