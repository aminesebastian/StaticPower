package theking530.staticpower.container.slots;

import net.minecraftforge.items.IItemHandler;

public class DigistoreSlot extends StaticPowerContainerSlot {
	private boolean managerPresent;

	public DigistoreSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean managerPresent) {
		super(itemHandler, index, xPosition, yPosition);
		this.managerPresent = managerPresent;
	}

}
