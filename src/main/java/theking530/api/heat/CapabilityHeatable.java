package theking530.api.heat;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityHeatable {
	@CapabilityInject(IHeatStorage.class)
	public static Capability<IHeatStorage> HEAT_STORAGE_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IHeatStorage.class);
	}
}
