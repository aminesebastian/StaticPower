package theking530.staticpower.cables.power.wire;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;

public class PowerWireNetworkModule extends AbstractCableNetworkModule {

	public PowerWireNetworkModule() {
		super(CableNetworkModuleTypes.POWER_WIRE_NETWORK_MODULE);
	}

	@Override
	public void preWorldTick(Level world) {
	}

	@Override
	public void tick(Level world) {
		System.out.println(this.Network.getGraph().getCables().size() + "  " + this.Network.getId());
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {

	}

	@Override
	public void readFromNbt(CompoundTag tag) {

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {

		return tag;
	}

	@Override
	public void getReaderOutput(List<Component> output) {

	}

}
