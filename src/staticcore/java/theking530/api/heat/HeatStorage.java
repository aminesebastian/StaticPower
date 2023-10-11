package theking530.api.heat;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticcore.utilities.math.SDMath;

public class HeatStorage implements IHeatStorage, INBTSerializable<CompoundTag> {
	protected float mass;
	protected float currentTemperature;
	protected float minimumTemperature;
	protected float overheatTemperature;
	protected float maximumTemperature;
	protected float conductivity;
	protected float specificHeat;

	protected boolean canHeat;
	protected boolean canCool;

	protected HeatTicker ticker;

	public HeatStorage(float mass, float specificHeat, float overheatThreshold, float maximumHeat, float conductivity) {
		this(mass, specificHeat, IHeatStorage.ABSOLUTE_ZERO, overheatThreshold, maximumHeat, conductivity, 0);
	}

	public HeatStorage(float mass, float specificHeat, float minimumThreshold, float overheatThreshold,
			float maximumHeat, float conductivity) {
		this(mass, specificHeat, minimumThreshold, overheatThreshold, maximumHeat, conductivity, 0);
	}

	public HeatStorage(float mass, float specificHeat, float minimumThreshold, float overheatThreshold,
			float maximumHeat, float conductivity, int meltdownRecoveryTicks) {
		this.mass = mass;
		this.specificHeat = specificHeat;
		this.maximumTemperature = maximumHeat;
		this.minimumTemperature = minimumThreshold;
		this.overheatTemperature = overheatThreshold;
		this.conductivity = conductivity;
		canHeat = true;
		canCool = true;
		ticker = new HeatTicker(this);
	}

	@Override
	public float getTemperature() {
		return currentTemperature;
	}

	@Override
	public float getMinimumOperatingThreshold() {
		return minimumTemperature;
	}

	public void setMinimumTemperatureThreshold(float minimumThreshold) {
		this.minimumTemperature = minimumThreshold;
	}

	@Override
	public float getOverheatThreshold() {
		return overheatTemperature;
	}

	@Override
	public float getMaximumTemperature() {
		return maximumTemperature;
	}

	public void setMaximumHeat(float newMax) {
		maximumTemperature = newMax;
		currentTemperature = Math.min(maximumTemperature, currentTemperature);
	}

	@Override
	public float getConductivity() {
		return conductivity;
	}

	public void setConductivity(float conductivity) {
		this.conductivity = conductivity;
	}

	public void setTemperature(float heat) {
		if (Float.isNaN(heat)) {
			return;
		}
		currentTemperature = SDMath.clamp(heat, ABSOLUTE_ZERO, maximumTemperature);
	}

	@Override
	public float heat(float heatFlux, HeatTransferAction action) {
		if (!canHeat || heatFlux <= 0) {
			return 0;
		}

		return handlePowerTransfer(heatFlux, action);
	}

	@Override
	public float cool(float heatFlux, HeatTransferAction action) {
		if (!canCool || heatFlux <= 0) {
			return 0;
		}

		return handlePowerTransfer(-heatFlux, action);
	}

	protected float handlePowerTransfer(float heatFlux, HeatTransferAction action) {
		if (heatFlux == 0) {
			return 0;
		}

		float temperatureDelta = HeatUtilities.calculateTemperatureDelta(heatFlux, getSpecificHeat(), getMass());
		float newTemperature = SDMath.clamp(currentTemperature + temperatureDelta, ABSOLUTE_ZERO,
				getMaximumTemperature());
		float actualDelta = newTemperature - currentTemperature;
		float usedPower = HeatUtilities.calculateHeatFluxRequiredForTemperatureChange(actualDelta, getSpecificHeat(),
				getMass());
		if (action == HeatTransferAction.EXECUTE) {
			setTemperature(newTemperature);
			if (actualDelta > 0) {
				getTicker().heated(actualDelta, usedPower);
			} else if (actualDelta < 0) {
				getTicker().cooled(-actualDelta, -usedPower);
			}
		}

		return usedPower;
	}

	public boolean isAtMaxHeat() {
		return currentTemperature == maximumTemperature;
	}

	public boolean isEmpty() {
		return currentTemperature == 0.0f;
	}

	public boolean isCanHeat() {
		return canHeat;
	}

	public void setCanHeat(boolean canHeat) {
		this.canHeat = canHeat;
	}

	public boolean isCanCool() {
		return canCool;
	}

	public void setCanCool(boolean canCool) {
		this.canCool = canCool;
	}

	@Override
	public HeatTicker getTicker() {
		return ticker;
	}

	@Override
	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	@Override
	public float getSpecificHeat() {
		return specificHeat;
	}

	public void setSpecificHeat(float specificHeat) {
		this.specificHeat = specificHeat;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		currentTemperature = nbt.getFloat("temp");
		minimumTemperature = nbt.getFloat("min_temp");
		overheatTemperature = nbt.getFloat("overheat_temp");
		maximumTemperature = nbt.getFloat("max_temp");
		conductivity = nbt.getFloat("conductivity");
		mass = nbt.getFloat("mass");
		specificHeat = nbt.getFloat("specific_heat");
		getTicker().deserializeNBT(nbt.getCompound("ticker"));
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();

		output.putFloat("temp", currentTemperature);
		output.putFloat("min_temp", minimumTemperature);
		output.putFloat("overheat_temp", overheatTemperature);
		output.putFloat("max_temp", maximumTemperature);
		output.putFloat("conductivity", conductivity);
		output.putFloat("mass", mass);
		output.putFloat("specific_heat", specificHeat);
		output.put("ticker", getTicker().serializeNBT());
		return output;
	}
}
