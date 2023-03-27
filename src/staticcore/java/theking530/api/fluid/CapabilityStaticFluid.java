package theking530.api.fluid;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityStaticFluid {
	public static final Capability<IStaticPowerFluidHandler> STATIC_FLUID_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
}
