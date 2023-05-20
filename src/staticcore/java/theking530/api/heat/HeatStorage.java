package theking530.api.heat;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticcore.utilities.math.SDMath;

public class HeatStorage implements IHeatStorage, INBTSerializable<CompoundTag> {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 20;

	protected float mass;
	protected float currentHeat;
	protected float minimumThreshold;
	protected float overheatThreshold;
	protected float maximumHeat;
	protected float conductivity;

	protected boolean canHeat;
	protected boolean canCool;

	protected HeatTicker ticker;

	public HeatStorage(float mass, float overheatThreshold, float maximumHeat, float conductivity) {
		this(mass, IHeatStorage.MINIMUM_TEMPERATURE, overheatThreshold, maximumHeat, conductivity, 0);
	}

	public HeatStorage(float mass, float minimumThreshold, float overheatThreshold, float maximumHeat,
			float conductivity) {
		this(mass, minimumThreshold, overheatThreshold, maximumHeat, conductivity, 0);
	}

	public HeatStorage(float mass, float minimumThreshold, float overheatThreshold, float maximumHeat,
			float conductivity, int meltdownRecoveryTicks) {
		this.mass = mass;
		this.maximumHeat = maximumHeat;
		this.minimumThreshold = minimumThreshold;
		this.overheatThreshold = overheatThreshold;
		this.conductivity = conductivity;
		canHeat = true;
		canCool = true;
		ticker = new HeatTicker(this);
	}

	@Override
	public float getCurrentTemperature() {
		return currentHeat;
	}

	@Override
	public float getMinimumHeatThreshold() {
		return minimumThreshold;
	}

	public void setMinimumHeatThreshold(int minimumThreshold) {
		this.minimumThreshold = minimumThreshold;
	}

	@Override
	public float getOverheatThreshold() {
		return overheatThreshold;
	}

	@Override
	public float getMaximumHeat() {
		return maximumHeat;
	}

	public void setMaximumHeat(float newMax) {
		maximumHeat = newMax;
		currentHeat = Math.min(maximumHeat, currentHeat);
	}

	@Override
	public float getConductivity() {
		return conductivity;
	}

	public void setConductivity(float conductivity) {
		this.conductivity = conductivity;
	}

	public void setCurrentHeat(float heat) {
		if (Float.isNaN(heat)) {
			return;
		}
		currentHeat = SDMath.clamp(heat, MINIMUM_TEMPERATURE, maximumHeat);
	}

	@Override
	public float heat(float amountToHeat, HeatTransferAction action) {
		if (!canHeat || amountToHeat <= 0) {
			return 0;
		}

		return handleThermalTransfer(amountToHeat, action);
	}

	@Override
	public float cool(float amountToCool, HeatTransferAction action) {
		if (!canCool || amountToCool <= 0) {
			return 0;
		}

		return handleThermalTransfer(-amountToCool, action);
	}

	protected float handleThermalTransfer(float amount, HeatTransferAction action) {
		if (amount == 0) {
			return 0;
		}

		float actualAmount = amount;
		if (amount > 0) {
			float remainingHeatCapacity = maximumHeat - currentHeat;
			actualAmount = Math.min(remainingHeatCapacity, amount);
		} else {
			float remainingCoolCapacity = MINIMUM_TEMPERATURE - getCurrentTemperature();
			actualAmount = -Math.min(Math.abs(remainingCoolCapacity), Math.abs(amount));
		}

		if (action == HeatTransferAction.EXECUTE) {
			setCurrentHeat(currentHeat + actualAmount);
			if (actualAmount > 0) {
				getTicker().heated(actualAmount);
			} else {
				getTicker().cooled(-actualAmount);
			}
		}

		return Math.abs(actualAmount);
	}

	public boolean isAtMaxHeat() {
		return currentHeat == maximumHeat;
	}

	public boolean isEmpty() {
		return currentHeat == 0.0f;
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
	public void deserializeNBT(CompoundTag nbt) {
		currentHeat = nbt.getFloat("current_heat");
		minimumThreshold = nbt.getFloat("minimum_heat");
		overheatThreshold = nbt.getFloat("overheat_threshold");
		maximumHeat = nbt.getFloat("maximum_heat");
		conductivity = nbt.getFloat("maximum_transfer_rate");
		getTicker().deserializeNBT(nbt.getCompound("ticker"));
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();

		output.putFloat("current_heat", currentHeat);
		output.putFloat("minimum_heat", minimumThreshold);
		output.putFloat("overheat_threshold", overheatThreshold);
		output.putFloat("maximum_heat", maximumHeat);
		output.putFloat("maximum_transfer_rate", conductivity);
		output.put("ticker", getTicker().serializeNBT());
		return output;
	}

	@Override
	public HeatTicker getTicker() {
		return ticker;
	}

	@Override
	public float getMass() {
		return mass;
	}
}
