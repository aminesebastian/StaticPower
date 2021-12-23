package theking530.api.attributes.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityAttributable {
	@CapabilityInject(IAttributable.class)
	public static Capability<IAttributable> ATTRIBUTABLE_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IAttributable.class);
	}
}
