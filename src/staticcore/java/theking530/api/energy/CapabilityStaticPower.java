package theking530.api.energy;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityStaticPower {
	public static final Capability<IStaticPowerStorage> STATIC_VOLT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
}
