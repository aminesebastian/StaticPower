package theking530.staticpower.cables.scaffold;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.network.modules.CableNetworkModule;
import theking530.staticpower.cables.network.modules.CableNetworkModuleTypes;

public class ScaffoldNetworkModule extends CableNetworkModule {

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
	}
}
