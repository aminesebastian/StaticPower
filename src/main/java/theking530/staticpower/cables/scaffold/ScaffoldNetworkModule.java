package theking530.staticpower.cables.scaffold;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;

public class ScaffoldNetworkModule extends AbstractCableNetworkModule {

	public ScaffoldNetworkModule() {
		super(CableNetworkModuleTypes.SCAFFOLD_NETWORK_MODULE);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		return tag;
	}

	@Override
	public void getReaderOutput(List<Component> components) {

	}

	@Override
	public void tick(Level world) {
		// TODO Auto-generated method stub

	}
}
