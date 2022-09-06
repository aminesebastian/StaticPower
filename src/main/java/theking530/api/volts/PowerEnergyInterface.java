package theking530.api.volts;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerEnergyInterface implements IEnergyStorage, IStaticVoltHandler {
	public enum EnergyType {
		STATIC_VOLT, FORGE_ENERGY
	}

	protected final IStaticVoltHandler staticVoltHandler;
	protected final IEnergyStorage energyStorage;
	protected final EnergyType actualType;

	public PowerEnergyInterface(IStaticVoltHandler staticVoltHandler) {
		this.staticVoltHandler = staticVoltHandler;
		energyStorage = null;
		actualType = EnergyType.STATIC_VOLT;
	}

	public PowerEnergyInterface(IEnergyStorage energyStorage) {
		this.energyStorage = energyStorage;
		this.staticVoltHandler = null;
		actualType = EnergyType.FORGE_ENERGY;
	}

	/******************************
	 * IStaticVoltHandler Interface
	 ******************************/
	@Override
	public long getStoredPower() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return staticVoltHandler.getStoredPower();
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return CapabilityStaticVolt.convertFEtomSV(energyStorage.getEnergyStored());
		}
		return 0;
	}

	@Override
	public long getCapacity() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return staticVoltHandler.getCapacity();
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return CapabilityStaticVolt.convertFEtomSV(energyStorage.getMaxEnergyStored());
		}
		return 0;
	}

	@Override
	public long receivePower(long power, boolean simulate) {
		if (actualType == EnergyType.STATIC_VOLT) {
			return staticVoltHandler.receivePower(power, simulate);
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			int recievedFE = energyStorage.receiveEnergy(CapabilityStaticVolt.convertmSVtoFE(power), simulate);
			return CapabilityStaticVolt.convertFEtomSV(recievedFE);
		}
		return 0;
	}

	@Override
	public long drainPower(long power, boolean simulate) {
		if (actualType == EnergyType.STATIC_VOLT) {
			return staticVoltHandler.drainPower(power, simulate);
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			int extractedFE = energyStorage.extractEnergy(CapabilityStaticVolt.convertmSVtoFE(power), simulate);
			return CapabilityStaticVolt.convertFEtomSV(extractedFE);
		}
		return 0;
	}

	@Override
	public boolean canRecievePower() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return staticVoltHandler.canRecievePower();
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return energyStorage.canReceive();
		}
		return false;
	}

	@Override
	public boolean canBeDrained() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return staticVoltHandler.canBeDrained();
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return energyStorage.canExtract();
		}
		return false;
	}

	/**************************
	 * IEnergyStorage Interface
	 **************************/
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (actualType == EnergyType.STATIC_VOLT) {
			long recievedSV = receivePower(CapabilityStaticVolt.convertFEtomSV(maxReceive), simulate);
			return CapabilityStaticVolt.convertmSVtoFE(recievedSV);
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (actualType == EnergyType.STATIC_VOLT) {
			long extractedSV = drainPower(CapabilityStaticVolt.convertFEtomSV(maxExtract), simulate);
			return CapabilityStaticVolt.convertmSVtoFE(extractedSV);
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return extractEnergy(maxExtract, simulate);
		}
		return 0;
	}

	@Override
	public int getEnergyStored() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return CapabilityStaticVolt.convertmSVtoFE(staticVoltHandler.getStoredPower());
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return energyStorage.getEnergyStored();
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return CapabilityStaticVolt.convertmSVtoFE(staticVoltHandler.getCapacity());
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return energyStorage.getMaxEnergyStored();
		}
		return 0;
	}

	@Override
	public boolean canExtract() {
		return canBeDrained();
	}

	@Override
	public boolean canReceive() {
		return canRecievePower();
	}

	public static @Nullable PowerEnergyInterface getFromItemStack(ItemStack stack) {
		// Skip empty stacks.
		if (stack.isEmpty()) {
			return null;
		}

		// Check for SV.
		IStaticVoltHandler svHandler = stack.getCapability(CapabilityStaticVolt.DEP_STATIC_VOLT_CAPABILITY).orElse(null);
		if (svHandler != null) {
			return new PowerEnergyInterface(svHandler);
		}

		// Check for FE.
		IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		if (energyStorage != null) {
			return new PowerEnergyInterface(energyStorage);
		}

		// If we make it this far, return null.
		return null;
	}

	@Override
	public long getMaxReceive() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return CapabilityStaticVolt.convertmSVtoFE(staticVoltHandler.getMaxReceive());
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return energyStorage.receiveEnergy(Integer.MAX_VALUE, true);
		}
		return 0;
	}

	@Override
	public long getMaxDrain() {
		if (actualType == EnergyType.STATIC_VOLT) {
			return CapabilityStaticVolt.convertmSVtoFE(staticVoltHandler.getMaxDrain());
		} else if (actualType == EnergyType.FORGE_ENERGY) {
			return energyStorage.extractEnergy(Integer.MAX_VALUE, true);
		}
		return 0;
	}
}
