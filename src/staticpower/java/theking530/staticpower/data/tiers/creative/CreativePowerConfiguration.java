package theking530.staticpower.data.tiers.creative;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;

public class CreativePowerConfiguration extends TierPowerConfiguration {

	public CreativePowerConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected double getDefaultPowerCapacity() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected List<StaticPowerVoltage> internalGetDefaultInputVoltageRange() {
		return Arrays.asList(StaticPowerVoltage.LOW, StaticPowerVoltage.EXTREME);
	}

	@Override
	protected double getDefaultMaximumPowerInput() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected StaticPowerVoltage getDefaultOutputVoltage() {
		return StaticPowerVoltage.LOW;
	}

	@Override
	protected StaticPowerVoltage internalGetMaximumBatteryInputVoltage() {
		return StaticPowerVoltage.BONKERS;
	}

	@Override
	protected double getDefaultMaximumPowerOutput() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected double getBatteryCapacity() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected double getBatteryMaximumPowerOutput() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected double getSolarPanelPowerGeneration() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected double getSolarPanelPowerStorage() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	protected int getTransfomerRatio() {
		return 4;
	}
}
