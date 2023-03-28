package theking530.api.attributes.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityAttributable {
	public static final Capability<IAttributable> CAPABILITY_ATTRIBUTABLE = CapabilityManager
			.get(new CapabilityToken<>() {
			});
}
