package theking530.api.team;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityTeamOwnable {
	public static final Capability<ITeamOwnable> TEAM_OWNABLE_CAPABILITY = CapabilityManager
			.get(new CapabilityToken<>() {
			});
}
