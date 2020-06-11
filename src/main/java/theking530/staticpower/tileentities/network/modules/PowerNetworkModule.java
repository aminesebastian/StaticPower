package theking530.staticpower.tileentities.network.modules;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.energy.StaticPowerFEStorage;
import theking530.staticpower.tileentities.cables.power.PowerCableWrapper;
import theking530.staticpower.tileentities.network.NetworkMapper;
import theking530.staticpower.tileentities.network.factories.modules.CableNetworkModuleTypes;

public class PowerNetworkModule extends AbstractCableNetworkModule {
	private StaticPowerFEStorage EnergyStorage;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT);
		EnergyStorage = new StaticPowerFEStorage(0, 5, 0);
		EnergyStorage.setCanExtract(false);
	}

	@Override
	public void tick(ServerWorld world) {
		if (EnergyStorage.getEnergyStored() > 0) {
			Network.updateGraph(world, Network.getOrigin());
			for (TileEntity te : Network.getGraph().getDestinations()) {
				IEnergyStorage energyStorage = te.getCapability(CapabilityEnergy.ENERGY).orElseGet(null);
				if (energyStorage != null) {
					if (energyStorage.canReceive()) {
						int supplied = energyStorage.receiveEnergy(EnergyStorage.getCurrentMaximumPowerOutput(), false);
						EnergyStorage.setCanExtract(true);
						EnergyStorage.extractEnergy(supplied, false);
						EnergyStorage.setCanExtract(false);
					}
				}
			}
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		EnergyStorage.setCapacity(mapper.getDiscoveredCables().stream().filter(p -> p instanceof PowerCableWrapper).mapToInt(p -> 10).sum());
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		EnergyStorage.readFromNbt(tag);
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		EnergyStorage.writeToNbt(tag);
		return tag;
	}

	public StaticPowerFEStorage getEnergyStorage() {
		return EnergyStorage;
	}
}
