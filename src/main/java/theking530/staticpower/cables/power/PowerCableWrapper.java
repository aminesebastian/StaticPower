package theking530.staticpower.cables.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableWrapper;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.factories.cables.CableTypes;
import theking530.staticpower.cables.network.factories.modules.CableNetworkModuleRegistry;
import theking530.staticpower.cables.network.factories.modules.CableNetworkModuleTypes;

public class PowerCableWrapper extends AbstractCableWrapper {

	public PowerCableWrapper(World world, BlockPos position) {
		super(world, position, CableTypes.BASIC_POWER);
	}

	@Override
	public void onNetworkJoined(CableNetwork network) {
		super.onNetworkJoined(network);
		if (!network.hasModule(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT)) {
			network.addModule(CableNetworkModuleRegistry.get().create(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT));
		}
	}
}
