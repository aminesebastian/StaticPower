package theking530.staticpower.cables.power.wire;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.network.modules.CableNetworkModuleTypes;
import theking530.staticpower.cables.power.PowerNetworkModule;

public class PowerWireNetworkModule extends PowerNetworkModule {

	public PowerWireNetworkModule() {
		super(CableNetworkModuleTypes.POWER_WIRE_NETWORK_MODULE);
	}

	@Override
	public void preWorldTick(Level world) {
		super.preWorldTick(world);
	}

	@Override
	public void tick(Level world) {
		super.tick(world);
//		for (ServerCable cable : this.Network.getGraph().getCables().values()) {
//			if (!cable.getSparseConnections().isEmpty()) {
//				System.out.println(cable.getSparseConnections().size());
//			}
//		}
//		System.out.println(this.Network.getGraph().getCables().size() + "  " + this.Network.getId());
	}

	@Override
	public void getReaderOutput(List<Component> output) {
		super.getReaderOutput(output);
	}

}
