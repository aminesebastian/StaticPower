package theking530.staticpower.tileentities;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

/**
 * @author Amine
 *
 */
public abstract class TileEntityMachine extends TileEntityConfigurable {
	public static final int DEFAULT_RF_CAPACITY = 2500;
	public static final int DEFAULT_POWER_TRANSFER = 50;

	public final EnergyStorageComponent energyStorage;

	public boolean isUpdateQueued = true;

	public TileEntityMachine(TileEntityType<?> tileEntityType) {
		super(tileEntityType);
		disableFaceInteraction();
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", DEFAULT_RF_CAPACITY, DEFAULT_POWER_TRANSFER, DEFAULT_POWER_TRANSFER));
	}

	/* ENERGY */
	public float getEnergyPercent() {
		float amount = energyStorage.getStorage().getEnergyStored();
		float capacity = energyStorage.getStorage().getMaxEnergyStored();
		return (amount / capacity);
	}

	public boolean hasPower() {
		return energyStorage.getStorage().getEnergyStored() > 0;
	}
}
