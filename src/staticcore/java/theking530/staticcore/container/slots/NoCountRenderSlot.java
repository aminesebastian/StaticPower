package theking530.staticcore.container.slots;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NoCountRenderSlot extends Slot {
	private Slot encapsulatedSlot;

	public NoCountRenderSlot(Slot slot) {
		super(slot.container, slot.getSlotIndex(), slot.x, slot.y);
		encapsulatedSlot = slot;
	}

	@Override
	public ItemStack getItem() {
		// Get the actual stack.
		ItemStack parent = encapsulatedSlot.getItem();
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
