package theking530.api.digistore;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityDigistoreInventory {
	@CapabilityInject(IDigistoreInventory.class)
	public static Capability<IDigistoreInventory> DIGISTORE_INVENTORY_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IDigistoreInventory.class);
	}
}
