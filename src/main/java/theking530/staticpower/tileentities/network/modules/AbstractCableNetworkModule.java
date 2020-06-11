package theking530.staticpower.tileentities.network.modules;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import theking530.staticpower.tileentities.network.CableNetwork;
import theking530.staticpower.tileentities.network.NetworkMapper;

public abstract class AbstractCableNetworkModule {
	protected CableNetwork Network;
	private final ResourceLocation Type;

	public AbstractCableNetworkModule(ResourceLocation type) {
		Type = type;
	}

	public void onNetworkGraphUpdated(NetworkMapper mapper) {

	}

	public void onNetworksJoined(CableNetwork other) {

	}

	public void onAddedToNetwork(CableNetwork network) {
		Network = network;
	}

	public ResourceLocation getType() {
		return Type;
	}

	public abstract void tick(ServerWorld world);

	public abstract void readFromNbt(CompoundNBT tag);

	public abstract CompoundNBT writeToNbt(CompoundNBT tag);
}
