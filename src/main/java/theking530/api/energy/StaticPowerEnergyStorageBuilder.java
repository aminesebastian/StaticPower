package theking530.api.energy;

import java.util.HashSet;
import java.util.Set;

public class StaticPowerEnergyStorageBuilder {
	protected double capacity;

	protected StaticVoltageRange inputVoltageRange;
	protected double maxInputCurrent;

	protected double outputVoltage;
	protected double maxOutputCurrent;

	protected CurrentType outputCurrentType;
	protected Set<CurrentType> acceptableCurrentTypes;

	public StaticPowerEnergyStorageBuilder() {
		this(0);
	}

	public StaticPowerEnergyStorageBuilder(double capacity) {
		this.capacity = capacity;
		this.acceptableCurrentTypes = new HashSet<>();
	}

	public StaticPowerEnergyStorageBuilder withInputParameters(StaticVoltageRange inputVoltageRange, double maxInputCurrent) {
		this.inputVoltageRange = inputVoltageRange;
		this.maxInputCurrent = maxInputCurrent;
		return this;
	}

	public StaticPowerEnergyStorageBuilder withOutputParameters(CurrentType currentType, double outputVoltage, double maxOutputCurrent) {
		this.outputCurrentType = currentType;
		this.outputVoltage = outputVoltage;
		this.maxOutputCurrent = maxOutputCurrent;
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
				maxOutputCurrent, outputCurrentType);
	}
}