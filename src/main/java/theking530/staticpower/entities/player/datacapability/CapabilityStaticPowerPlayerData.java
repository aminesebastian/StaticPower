package theking530.staticpower.entities.player.datacapability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityStaticPowerPlayerData {
	@CapabilityInject(IStaticPowerPlayerData.class)
	public static Capability<IStaticPowerPlayerData> PLAYER_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IStaticPowerPlayerData.class);
	}
}
