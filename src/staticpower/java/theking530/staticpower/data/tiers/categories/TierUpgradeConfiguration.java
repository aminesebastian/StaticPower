package theking530.staticpower.data.tiers.categories;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticpower.StaticPower;

public abstract class TierUpgradeConfiguration {
	public final ConfigValue<Integer> upgradeOrdinal;
	/********************
	 * Processing Upgrade
	 ********************/
	public final ConfigValue<Double> processingSpeedUpgrade;
	public final ConfigValue<Double> processingSpeedPowerCost;

	/**************
	 * Tank Upgrade
	 **************/
	public final ConfigValue<Double> tankCapacityUpgrade;

	/***************
	 * Range Upgrade
	 ***************/
	public final ConfigValue<Double> rangeUpgrade;

	/***************
	 * Power Upgrade
	 ***************/
	public final ConfigValue<Double> powerUpgrade;
	public final ConfigValue<Double> powerIOUpgrade;

	/************************
	 * Heat Capacity Upgrade
	 ************************/
	public final ConfigValue<Float> heatCapacityUpgrade;

	/************************
	 * Heat Conductivity Upgrade
	 ************************/
	public final ConfigValue<Float> heatConductivityUpgrade;

	/***************************
	 * Output Multiplier Upgrade
	 ***************************/
	public final ConfigValue<Double> outputMultiplierUpgrade;
	public final ConfigValue<Double> outputMultiplierPowerCostUpgrade;

	/********************
	 * Centrifuge Upgrade
	 ********************/
	public final ConfigValue<Integer> maxCentrifugeSpeedUpgrade;
	public final ConfigValue<Double> centrifugeUpgradedPowerIncrease;

	public TierUpgradeConfiguration(ForgeConfigSpec.Builder builder) {
		upgradeOrdinal = builder.comment(
				"The upgrade ordinal of this tier. Higher value will take precedence. For example, if a machine has both Basic and Energized power upgrades installed, the Energized upgrades will be used when calculating power values because it has the higher upgrade ordinal. In the case of a tie, it comes down to which one appears in later in the upgrade slots.")
				.translation(StaticPower.MOD_ID + ".config." + "upgradeOrdinal").define("UpgradeOrdinal", this.getUpgradeOrdinal());

		/************************
		 * Heat Conductivity Upgrade
		 ************************/
		builder.push("Heat");
		heatCapacityUpgrade = builder.comment("The heat capacity for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "heatCapacityUpgrade").define("HeatCapacityUpgrade", this.getHeatCapacityUpgrade());

		heatConductivityUpgrade = builder.comment("The heat conductivtiy boost for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "heatConductivityUpgrade").define("HeatConductivityUpgrade", this.getHeatConductivityUpgrade());
		builder.pop();

		/********************
		 * Processing Upgrade
		 ********************/
		builder.push("Processing");
		processingSpeedUpgrade = builder.comment("The processing speed upgrade for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "processingSpeedUpgrade").define("ProcessingSpeedUpgrade", this.getProcessingSpeedUpgrade());

		processingSpeedPowerCost = builder.comment("The processing speed power cost penality for a full upgrade stack of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "processingSpeedPowerCost").define("ProcessingSpeedPowerCost", this.getProcessingSpeedPowerCost());
		builder.pop();

		/********************
		 * Power Upgrade
		 ********************/
		builder.push("Power");
		powerUpgrade = builder.comment("The power upgrade multiplier of a power upgrade for a full stack of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "powerUpgrade").define("PowerUpgrade", this.getPowerUpgrade());

		powerIOUpgrade = builder.comment("The power I/O (input/output) upgrade multiplier for a full stack of a power upgrade of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "powerIOUpgrade").define("PowerIOUpgrade", this.getPowerIoUpgrade());
		builder.pop();

		/********************
		 * Tank Upgrade
		 ********************/
		builder.push("Tank");
		tankCapacityUpgrade = builder.comment("The tank capacity multiplier for a full stack of a tank upgrade of this tier (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "tankCapacityUpgrade").define("TankCapacityUpgrade", this.getTankCapacityUpgrade());
		builder.pop();

		/********************
		 * Range Upgrade
		 ********************/
		builder.push("Range");
		rangeUpgrade = builder.comment("The increase in radius in percent for a single range upgrade of this tier.").translation(StaticPower.MOD_ID + ".config." + "rangeUpgrade")
				.define("RangeUpgrade", this.getRangeUpgrade());
		builder.pop();

		/********************
		 * Centrifuge Upgrade
		 ********************/
		builder.push("Centrifuge");
		maxCentrifugeSpeedUpgrade = builder.comment("The new maximum speed set by a centrifuge upgrade of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "maxCentrifugeSpeedUpgrade").define("MaxCentrifugeSpeedUpgrade", this.getMaxCentrifugeSpeedUpgrade());

		centrifugeUpgradedPowerIncrease = builder.comment("The amount of increased power cost due to a centrifuge upgrade of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "centrifugeUpgradedPowerIncrease")
				.define("CentrifugeUpgradedPowerIncrease", this.getCentrifugeUpgradedPowerIncrease());
		builder.pop();

		/********************
		 * Output Upgrade
		 ********************/
		builder.push("Output_Multiplier");
		outputMultiplierUpgrade = builder
				.comment("The multiplier applied to the output change of any probability outputs for a full stack of an output multiplier upgrade (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "outputMultiplierUpgrade").define("OutputMultiplierUpgrade", this.getOutputMultiplierUpgrade());

		outputMultiplierPowerCostUpgrade = builder.comment("The power usage increase for a full stack of an output multiplier upgrade (as a percentage 1.0+).")
				.translation(StaticPower.MOD_ID + ".config." + "outputMultiplierPowerCostUpgrade")
				.define("OutputMultiplierPowerCostUpgrade", this.getOutputMultiplierPowerCostUpgrade());
		builder.pop();
	}

	protected abstract int getUpgradeOrdinal();

	protected abstract int getMaxCentrifugeSpeedUpgrade();

	protected abstract double getCentrifugeUpgradedPowerIncrease();

	protected abstract double getOutputMultiplierPowerCostUpgrade();

	protected abstract double getOutputMultiplierUpgrade();

	public abstract float getHeatCapacityUpgrade();

	public abstract float getHeatConductivityUpgrade();

	protected abstract double getPowerUpgrade();

	protected abstract double getPowerIoUpgrade();

	protected abstract double getRangeUpgrade();

	protected abstract double getTankCapacityUpgrade();

	protected abstract double getProcessingSpeedUpgrade();

	protected abstract double getProcessingSpeedPowerCost();
}
