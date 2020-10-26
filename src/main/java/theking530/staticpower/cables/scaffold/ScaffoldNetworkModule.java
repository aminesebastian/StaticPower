package theking530.staticpower.cables.scaffold;

import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;

public class ScaffoldNetworkModule extends AbstractCableNetworkModule {

	public ScaffoldNetworkModule() {
		super(CableNetworkModuleTypes.SCAFFOLD_NETWORK_MODULE);
	}

	@Override
	public void tick(World world) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {

	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {

	}

	@Override
	public void readFromNbt(CompoundNBT tag) {

	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		return tag;
	}

	@Override
	public void getReaderOutput(List<ITextComponent> components) {

	}
}
