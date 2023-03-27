package theking530.api.energy;

import java.util.HashSet;
import java.util.Set;

public class StaticPowerEnergyStorageBuilder {
	protected double capacity;

	protected StaticVoltageRange inputVoltageRange;
	protected double maxInputCurrent;

	protected StaticPowerVoltage outputVoltage;
	protected double maxOutputCurrent;

	protected boolean canAcceptExternalPower;
	protected boolean canOutputExternalPower;

	protected CurrentType outputCurrentType;
	protected Set<CurrentType> acceptableCurrentTypes;

	protected boolean arePowerLimitsCumulative;

	public StaticPowerEnergyStorageBuilder(boolean arePowerLimitsCumulative) {
		this(0, arePowerLimitsCumulative);
	}

	public StaticPowerEnergyStorageBuilder(double capacity, boolean arePowerLimitsCumulative) {
		this.capacity = capacity;
		this.acceptableCurrentTypes = new HashSet<>();
		this.arePowerLimitsCumulative = arePowerLimitsCumulative;
	}

	public StaticPowerEnergyStorageBuilder withInputParameters(StaticVoltageRange inputVoltageRange, double maxInputCurrent, boolean canAcceptExternalPower) {
		this.inputVoltageRange = inputVoltageRange;
		this.maxInputCurrent = maxInputCurrent;
		this.canAcceptExternalPower = canAcceptExternalPower;
		return this;
	}

	public StaticPowerEnergyStorageBuilder withOutputParameters(CurrentType currentType, StaticPowerVoltage outputVoltage, double maxOutputCurrent,
			boolean canOutputExternalPower) {
		this.outputCurrentType = currentType;
		this.outputVoltage = outputVoltage;
		this.maxOutputCurrent = maxOutputCurrent;
		this.canOutputExternalPower = canOutputExternalPower;
		return this;
	}

	public StaticPowerEnergyStorageBuilder withAcceptableInputCurrentType(CurrentType... currentTypes) {
		for (CurrentType type : currentTypes) {
			acceptableCurrentTypes.add(type);
		}
		return this;
	}

	public StaticPowerStorage build() {
		return new StaticPowerStorage(capacity, inputVoltageRange, maxInputCurrent, acceptableCurrentTypes.toArray(new CurrentType[acceptableCurrentTypes.size()]), outputVoltage,
				maxOutputCurrent, outputCurrentType, canAcceptExternalPower, canOutputExternalPower, arePowerLimitsCumulative);
	}
}