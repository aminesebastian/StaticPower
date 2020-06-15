package theking530.staticpower.tileentities.cables.network.modules;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.network.CableNetwork;
import theking530.staticpower.tileentities.cables.network.NetworkMapper;

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

	public abstract void tick(World world);

	public abstract void readFromNbt(CompoundNBT tag);

	public abstract CompoundNBT writeToNbt(CompoundNBT tag);
}
