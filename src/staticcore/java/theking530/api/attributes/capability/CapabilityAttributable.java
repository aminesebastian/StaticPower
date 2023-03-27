package theking530.api.attributes.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityAttributable {
	public static final Capability<IAttributable> ATTRIBUTABLE_CAPABILITY = CapabilityManager
			.get(new CapabilityToken<>() {
			});
}
