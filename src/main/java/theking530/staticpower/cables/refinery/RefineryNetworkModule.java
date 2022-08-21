package theking530.staticpower.cables.refinery;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import theking530.api.power.StaticVoltAutoConverter;
import theking530.api.power.StaticVoltHandler;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.tileentities.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.tileentities.powered.refinery.controller.TileEntityRefinery;

public class RefineryNetworkModule extends AbstractCableNetworkModule {
	private final StaticVoltAutoConverter energyInterface;
	private final StaticVoltHandler storage;
	private TileEntityRefinery controller;

	public RefineryNetworkModule() {
		super(CableNetworkModuleTypes.REFINERY_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		storage = new StaticVoltHandler(10000, Integer.MAX_VALUE, Integer.MAX_VALUE);
		// No one should extract power from the network, we only provide it.
		storage.setCanDrain(false);
		// Create the interface.
		energyInterface = new StaticVoltAutoConverter(storage);
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		controller = null;

		// Cache all the refiner in the network.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Get the cable's tile entity.
			BlockEntity te = Network.getWorld().getChunkAt(cable.getPos()).getBlockEntity(cable.getPos(), LevelChunk.EntityCreationType.QUEUED);
			// If it's not null.
			if (te != null) {
				// If this is also a manager, set the manager present to true.
				// If we already had a controller set, ignore this.
				// TODO: Error message to show that there are multiple controllers attached.
				if (te instanceof TileEntityDigistoreManager) {
					if (controller == null) {
						controller = (TileEntityRefinery) te;
					}
				}
			}
		}
	}

	@Override
	public void tick(Level world) {
		
	}

	public StaticVoltAutoConverter getEnergyAutoConverter() {
		return energyInterface;
	}

	public StaticVoltHandler getEnergyStorage() {
		return storage;
	}

	public boolean isControllerPresent() {
		if (controller == null) {
			Network.updateGraph(Network.getWorld(), Network.getOrigin());
		}
		return controller != null;
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		storage.deserializeNBT(tag.getCompound("energy_storage"));
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		tag.put("energy_storage", storage.serializeNBT());
		return tag;
	}

	@Override
	public void getReaderOutput(List<Component> components) {

	}
}
