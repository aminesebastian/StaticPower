package theking530.staticpower.tileentities.digistorenetwork;

import java.util.concurrent.Callable;

public class DigistoreInventoryFactory implements Callable<IDigistoreInventory> {
	@Override
	public IDigistoreInventory call() throws Exception {
		return new DigistoreInventory(0, 0);
	}
}
