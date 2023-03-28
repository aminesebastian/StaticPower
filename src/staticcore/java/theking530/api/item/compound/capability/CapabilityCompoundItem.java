package theking530.api.item.compound.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityCompoundItem {
	public static final Capability<ICompoundItem> CAPABILITY_COMPOUND_ITEM = CapabilityManager
			.get(new CapabilityToken<>() {
			});
}
