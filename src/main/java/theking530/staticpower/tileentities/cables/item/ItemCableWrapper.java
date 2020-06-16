package theking530.staticpower.tileentities.cables.item;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.network.CableNetwork;
import theking530.staticpower.tileentities.cables.network.factories.cables.CableTypes;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleRegistry;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleTypes;

public class ItemCableWrapper extends AbstractCableWrapper {

	public ItemCableWrapper(World world, BlockPos position) {
		super(world, position, CableTypes.BASIC_ITEM);
	}

	@Override
	public void onNetworkJoined(CableNetwork network, boolean updateBlock) {
		super.onNetworkJoined(network, updateBlock);
		if (!network.hasModule(CableNetworkModuleTypes.ITEM_NETWORK_ATTACHMENT)) {
			network.addModule(CableNetworkModuleRegistry.get().create(CableNetworkModuleTypes.ITEM_NETWORK_ATTACHMENT));
		}
	}
}
