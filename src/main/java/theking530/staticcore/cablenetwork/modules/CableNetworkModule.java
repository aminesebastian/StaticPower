package theking530.staticcore.cablenetwork.modules;

import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;

public abstract class CableNetworkModule {
	protected CableNetwork Network;
	private final CableNetworkModuleType type;

	public CableNetworkModule(CableNetworkModuleType type) {
		this.type = type;
	}

	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {

	}

	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {

	}

	public void onAddedToNetwork(CableNetwork network) {
		this.Network = network;
	}

	public void onJoinedOtherNetwork(CableNetwork network) {

	}

	public CableNetworkModuleType getType() {
		return type;
	}

	public CableNetwork getNetwork() {
		return Network;
	}

	public void onNetworkUpdatesDisabled() {

	}

	public void onNetworkUpdatesEnabled() {

	}

	public boolean canAcceptCable(ServerCable currentNetworkCable, ServerCable newCable) {
		return true;
	}

	public abstract void getReaderOutput(List<Component> components, BlockPos pos);

	public void preWorldTick(Level world) {
	}

	public abstract void tick(Level world);

	public void readFromNbt(CompoundTag tag) {
	}

	public CompoundTag writeToNbt(CompoundTag tag) {
		return tag;
	}

	public void recieveCrossNetworkUpdate(CableNetwork sendingNetwork, Set<CableNetwork> previousNetworks) {

	}
}
