package theking530.staticcore.cablenetwork.modules;

import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.CableNetwork;
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

	/**
	 * This method is raised when this module is first added to a network. This is
	 * called when it is the first type of this module added to the network, meaning
	 * there is not need to do any merging logic here.
	 * 
	 * @param network
	 */
	public void onFirstAddedToNetwork(CableNetwork network) {
		this.Network = network;
	}

	/**
	 * This method is raised when this module is added to another existing network.
	 * The implication is the original owning network for this module has now merged
	 * with another module that MAY have this module. You can perform logic to merge
	 * the two networks here. This instance will be thrown away in favor of the
	 * instance on the incoming network.
	 * 
	 * @param network
	 */
	public void onJoinedOtherNetwork(CableNetwork network) {
		this.Network = network;

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

	public boolean canAcceptCable(Cable currentNetworkCable, Cable newCable) {
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
