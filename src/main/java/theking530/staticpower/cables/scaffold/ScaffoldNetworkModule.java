package theking530.staticpower.cables.scaffold;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticpower.init.cables.ModCableModules;

public class ScaffoldNetworkModule extends CableNetworkModule {

	public ScaffoldNetworkModule() {
		super(ModCableModules.Scaffold.get());
	}

	@Override
	public void readFromNbt(CompoundTag tag) {

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		return tag;
	}

	@Override
	public void getReaderOutput(List<Component> components, BlockPos pos) {

	}

	@Override
	public void tick(Level world) {
	}
}
