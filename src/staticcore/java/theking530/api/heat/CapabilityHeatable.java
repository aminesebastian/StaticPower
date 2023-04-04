package theking530.api.heat;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityHeatable {
	public static final Capability<IHeatStorage> HEAT_STORAGE_CAPABILITY = CapabilityManager
			.get(new CapabilityToken<>() {
			});
}
