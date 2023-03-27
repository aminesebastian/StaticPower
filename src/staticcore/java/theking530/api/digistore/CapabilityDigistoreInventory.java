package theking530.api.digistore;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityDigistoreInventory {
	public static final Capability<IDigistoreInventory> DIGISTORE_INVENTORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
}
