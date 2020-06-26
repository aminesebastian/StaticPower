package theking530.staticpower.cables.power;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.energy.StaticPowerFEStorage;

public class PowerNetworkModule extends AbstractCableNetworkModule {
	private StaticPowerFEStorage EnergyStorage;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		EnergyStorage = new StaticPowerFEStorage(0, 50, 50);
		EnergyStorage.setCanExtract(false);
	}

	@Override
	public void tick(World world) {
		if (EnergyStorage.getEnergyStored() > 0) {
			for (DestinationWrapper wrapper : Network.getGraph().getDestinations().values()) {
				IEnergyStorage energyStorage = wrapper.getTileEntity().getCapability(CapabilityEnergy.ENERGY).orElse(null);
				if (energyStorage != null) {
					if (energyStorage.canReceive()) {
						int supplied = energyStorage.receiveEnergy(EnergyStorage.getCurrentMaximumPowerOutput(), false);
						if (supplied > 0) {
							EnergyStorage.setCanExtract(true);
							EnergyStorage.extractEnergy(supplied, false);
							EnergyStorage.setCanExtract(false);
						}
					}
				}
			}
		}
	}

	public void onNetworksJoined(CableNetwork other) {
		if (other.hasModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)) {
			PowerNetworkModule module = (PowerNetworkModule) other.getModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
			module.getEnergyStorage().receiveEnergy(EnergyStorage.getEnergyStored(), false);
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		EnergyStorage.setCapacity(mapper.getDiscoveredCables().stream().filter(p -> p.supportsNetworkModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)).mapToInt(p -> 10).sum());
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
