package theking530.staticpower.entities.player.datacapability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityStaticPowerPlayerData {
	public static Capability<IStaticPowerPlayerData> PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
}
