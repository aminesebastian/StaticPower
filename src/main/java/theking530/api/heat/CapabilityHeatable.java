package theking530.api.heat;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityHeatable {
	public static final int mC_TO_C = 1000;
	public static Capability<IHeatStorage> HEAT_STORAGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IHeatStorage.class);
	}

	public static int convertMilliHeatToHeat(int mC) {
		return mC / mC_TO_C;
	}

	public static int convertHeatToMilliHeat(int C) {
		return C * mC_TO_C;
	}
}
