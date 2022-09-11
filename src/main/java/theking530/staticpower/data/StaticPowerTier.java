package theking530.staticpower.data;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;
import theking530.staticpower.data.tiers.categories.TierUpgradeConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierCableAttachmentConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierHeatCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public abstract class StaticPowerTier {
	/****************
	 * Tier Settings
	 ****************/
	public final ConfigValue<String> tierId;
	public final ConfigValue<String> unlocalizedTierName;

	/***********
	 * Digistore
	 ***********/
	public final ConfigValue<Integer> digistoreCardCapacity;

	/*******
	 * Pump
	 *******/
	public final ConfigValue<Integer> pumpRate;

	/********************
	 * Heat Configuration
	 ********************/
	public final ConfigValue<Integer> heatSinkOverheatTemperature;
	public final ConfigValue<Integer> heatSinkMaximumTemperature;
	public final ConfigValue<Float> heatSinkConductivity;

	public final ConfigValue<Integer> heatSinkElectricHeatGeneration;
	public final ConfigValue<Integer> heatSinkElectricHeatPowerUsage;

	/*********************
	 * Item Configuration
	 *********************/
	public final ConfigValue<Double> portableBatteryCapacity;
	public final ConfigValue<List<Double>> portableBatteryInputVoltageRange;
	public final ConfigValue<Double> portableBatteryMaxOutputCurrent;
	public final ConfigValue<Double> portableBatteryOutputVoltage;

	/***********************
	 * Machine Configuration
	 ***********************/
	public final ConfigValue<Integer> defaultMachineOverheatTemperature;
	public final ConfigValue<Integer> defaultMachineMaximumTemperature;
	public final ConfigValue<Integer> defaultTankCapacity;

	/**********
	 * Conveyer
	 **********/
	public final ConfigValue<Double> conveyorSpeedMultiplier;
	public final ConfigValue<Integer> conveyorSupplierStackSize;
	public final ConfigValue<Integer> conveyorExtractorStackSize;

	/****************
	 * Turbine Blades
	 ****************/
	public final ConfigValue<Integer> turbineBladeDurabilityTicks;
	public final ConfigValue<Double> turbineBladeGenerationBoost;

	/*******
	 * Misc
	 *******/
	public final ConfigValue<Integer> capsuleCapacity;
	public final ConfigValue<Integer> itemFilterSlots;

	public final TierPowerConfiguration powerConfiguration;
	public final TierUpgradeConfiguration upgradeConfiguration;
	public final TierToolConfiguration toolConfiguration;

	public final TierCableAttachmentConfiguration cableAttachmentConfiguration;
	public final TierFluidCableConfiguration cableFluidConfiguration;
	public final TierHeatCableConfiguration cableHeatConfiguration;
	public final TierItemCableConfiguration cableItemConfiguration;
	public final TierPowerCableConfiguration cablePowerConfiguration;

	public StaticPowerTier(ForgeConfigSpec.Builder builder) {
		// Establish field for the tier Id.
		tierId = builder.comment("The unique id of the tier in the format of 'MOD_ID:TIER_NAME'.").translation(StaticPower.MOD_ID + ".config." + "tierId").define("TierId",
				getTierId().toString());

		// Establish a field for the unlocalized name.
		unlocalizedTierName = builder.comment("The unlocalized name of the tier.").translation(StaticPower.MOD_ID + ".config." + "unlocalizedTierName")
				.define("UnlocalizedTierName", getUnlocalizedName());

		builder.push("Power");
		powerConfiguration = createPowerConfiguration(builder);
		builder.pop();

		builder.push("Upgrades");
		upgradeConfiguration = createUpgradeConfiguration(builder);
		builder.pop();

		builder.push("Tools");
		toolConfiguration = createToolConfiguration(builder);
		builder.pop();

		builder.push("Cables");
		{
			builder.push("Attachments");
			cableAttachmentConfiguration = createCableAttachmentConfiguration(builder);
			builder.pop();

			builder.push("Fluid");
			cableFluidConfiguration = createFluidCableConfiguration(builder);
			builder.pop();

			builder.push("Heat");
			cableHeatConfiguration = createHeatCableConfiguration(builder);
			builder.pop();

			builder.push("Item");
			cableItemConfiguration = createItemCableConfiguration(builder);
			builder.pop();

			builder.push("Power");
			cablePowerConfiguration = createPowerCableConfiguration(builder);
			builder.pop();
		}
		builder.pop();

		builder.push("Digistore");
		digistoreCardCapacity = builder.comment("The number of items that can be contained in a regular digistore card of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "digistoreCardCapacity").define("DigistoreCardCapacity", getDigistoreCardCapacity());
		builder.pop();

		builder.push("Machines");

		defaultMachineOverheatTemperature = builder.comment("The temperature at which a machine of this tier overheats and stops processing (in mC [1C = 1000mC]).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachineOverheatTemperature")
				.define("DefaultMachineOverheatTemperature", getDefaultMachineOverheatTemperature());
		defaultMachineMaximumTemperature = builder.comment("The maximum amount of heat a machine of this tier can contain (in mC [1C = 1000mC]).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultMachineMaximumTemperature")
				.define("DefaultMachineMaximumTemperature", getDefaultMachineMaximumTemperature());
		defaultTankCapacity = builder.comment("The base amount of fluid a machine of this tier can store (in mB [1B = 1000mB]).")
				.translation(StaticPower.MOD_ID + ".config." + "defaultTankCapacity").define("DefaultTankCapacity", this.getDefaultTankCapacity());

		builder.push("Conveyor");
		conveyorSpeedMultiplier = builder.comment("The speed multitplier applied to conveyors of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "conveyorSpeedMultiplier").define("ConveyorSpeedMultiplier", getConveyorSpeedMultiplier());

		conveyorSupplierStackSize = builder.comment("The maximum stack size suppliers of this tier can consume at a time.")
				.translation(StaticPower.MOD_ID + ".config." + "conveyorSupplierStackSize").define("ConveyorSupplierStackSize", getConveyorSupplierStackSize());

		conveyorExtractorStackSize = builder.comment("The maximum stack size that an extractor of this tier can extract from an adjacent inventory.")
				.translation(StaticPower.MOD_ID + ".config." + "conveyorExtractorStackSize").define("ConveyorExtractorStackSize", getConveyorExtractorStackSize());

		builder.pop();

		builder.push("Pump");
		pumpRate = builder
				.comment("The amount of ticks that will elapse between each pump operation. The higher this number, the slower the pump will operate (20 ticks == 1 second).")
				.translation(StaticPower.MOD_ID + ".config." + "pumpRate").define("PumpRate", this.getPumpRate());
		builder.pop();

		builder.push("Turbine Blades");
		turbineBladeDurabilityTicks = builder.comment("The amount of ticks that a turbine blade will last (20 ticks == 1 second).")
				.translation(StaticPower.MOD_ID + ".config." + "turbineBladeDurabilityTicks").define("TurbineBladeDurabilityTicks", this.getTurbineBladeDurabilityTicks());
		turbineBladeGenerationBoost = builder.comment("The multiplier applied to the generation of the turbine these blades are installed in..")
				.translation(StaticPower.MOD_ID + ".config." + "turbineBladeGenerationBoost").define("TurbineBladeGenerationBoost", this.getTurbineBladeGenerationBoost());
		builder.pop();

		builder.push("Heatsink");
		heatSinkOverheatTemperature = builder.comment("The temperature above which this heatsink can no longer cool down other entities.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkOverheatTemperature").define("HeatSinkOverheatTemperature", this.getHeatsinkOverheatTemperature());

		heatSinkMaximumTemperature = builder.comment("The temperature above which this heatsink will be destroyed.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkMaximumTemperature").define("HeatSinkMaximumTemperature", this.getHeatsinkMaximumTemperature());

		heatSinkConductivity = builder.comment("The conductivity multiplier for a heatsink of this tier. The higher it is, the faster it is able to dissipate heat.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkConductivity").define("HeatSinkConductivity", this.getHeatSinkConductivity());

		heatSinkElectricHeatGeneration = builder.comment("The amount of heat generated per tick by a heatsink of this tier when supplied with power.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkElectricHeatGeneration").define("HeatSinkElectricHeatGeneration", this.getHeatSinkElectricHeatGeneration());

		heatSinkElectricHeatPowerUsage = builder.comment("The amount of power used per tick to generate heat in a heatsink of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "heatSinkElectricHeatPowerUsage").define("HeatSinkElectricHeatPowerUsage", this.getHeatSinkElectricHeatPowerUsage());
		builder.pop();
		builder.pop();

		/********
		 * Items
		 ********/
		builder.push("Items");
		capsuleCapacity = builder.comment("The amount of fluid that can be stored in a fluid capsule of this tier.")
				.translation(StaticPower.MOD_ID + ".config." + "capsuleCapacity").define("CapsuleCapacity", this.getCapsuleCapacity());
		itemFilterSlots = builder.comment("The number of slots that exist on an item filter of this tier (not the filter attachment, the actual item).")
				.translation(StaticPower.MOD_ID + ".config." + "itemFilterSlots").define("ItemFilterSlots", this.getItemFilterSlots());

		builder.push("Battery");
		portableBatteryCapacity = builder.comment("The amount of power that can be stored in a portable battery of this tier (in SW).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryCapacity").define("PortableBatteryCapacity", this.getPortableBatteryCapacity());
		portableBatteryInputVoltageRange = builder.comment("The voltage range that can be used to charge a battery of this tier (in SV).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryInputVoltageRange")
				.define("PortableBatteryInputVoltageRange", internalGetPortableBatteryChargingVoltage());
		portableBatteryMaxOutputCurrent = builder.comment("The maximum input current for a battery of this tier (in SA).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryMaxOutputCurrent")
				.define("PortableBatteryMaxOutputCurrent", this.getPortableBatteryMaxOutputCurrent());
		portableBatteryOutputVoltage = builder.comment("The ouput voltage for a battery of this tier (in SV).")
				.translation(StaticPower.MOD_ID + ".config." + "portableBatteryOutputVoltage").define("PortableBatteryOutputVoltage", this.getPortableBatteryOutputVoltage());
		builder.pop();

		/********
		 * Tools
		 ********/
		builder.push("Tools");

		builder.pop();

		builder.push("Upgrade");

		builder.pop();
		builder.pop();
	}

	protected abstract ResourceLocation getTierId();

	protected abstract String getUnlocalizedName();

	protected TierPowerConfiguration createPowerConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected TierUpgradeConfiguration createUpgradeConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected TierToolConfiguration createToolConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		return null;
	}

	protected int getDefaultTankCapacity() {
		return 0;
	}

	protected int getCapsuleCapacity() {
		return 0;
	}

	protected int getDefaultMachineOverheatTemperature() {
		return 0;
	}

	protected int getDefaultMachineMaximumTemperature() {
		return 0;
	}

	protected double getConveyorSpeedMultiplier() {
		return 1;
	}

	protected int getConveyorSupplierStackSize() {
		return 1;
	}

	protected int getConveyorExtractorStackSize() {
		return 1;
	}

	protected int getHeatSinkElectricHeatGeneration() {
		return 0;
	}

	protected int getHeatSinkElectricHeatPowerUsage() {
		return 0;
	}

	protected int getHeatsinkOverheatTemperature() {
		return 0;
	}

	protected int getHeatsinkMaximumTemperature() {
		return 0;
	}

	protected float getHeatSinkConductivity() {
		return 0;
	}

	protected int getItemFilterSlots() {
		return 0;
	}

	protected double getPortableBatteryCapacity() {
		return 0;
	}

	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(0.0, 0.0);
	}

	public StaticVoltageRange getPortableBatteryChargingVoltage() {
		return new StaticVoltageRange(portableBatteryInputVoltageRange.get().get(0), portableBatteryInputVoltageRange.get().get(1));
	}

	protected double getPortableBatteryMaxOutputCurrent() {
		return 0;
	}

	protected double getPortableBatteryOutputVoltage() {
		return 0;
	}

	protected int getDigistoreCardCapacity() {
		return 0;
	}

	protected int getPumpRate() {
		return 0;
	}

	protected double getTurbineBladeGenerationBoost() {
		return 0.0;
	}

	protected int getTurbineBladeDurabilityTicks() {
		return 0;
	}
}
