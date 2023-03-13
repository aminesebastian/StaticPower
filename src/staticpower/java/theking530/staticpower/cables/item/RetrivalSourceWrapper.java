package theking530.staticpower.cables.item;

import theking530.staticcore.cablenetwork.data.DestinationWrapper;

public class RetrivalSourceWrapper {
	private final DestinationWrapper destinationWrapper;
	private final int inventorySlot;

	public RetrivalSourceWrapper(DestinationWrapper destinationWrapper, int inventorySlot) {
		this.destinationWrapper = destinationWrapper;
		this.inventorySlot = inventorySlot;
	}

	public DestinationWrapper getDestinationWrapper() {
		return destinationWrapper;
	}

	public int getInventorySlot() {
		return inventorySlot;
	}

}
