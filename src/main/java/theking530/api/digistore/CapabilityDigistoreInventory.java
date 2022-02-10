package theking530.api.digistore;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityDigistoreInventory {
	public static Capability<IDigistoreInventory> DIGISTORE_INVENTORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IDigistoreInventory.class);
	}
}
