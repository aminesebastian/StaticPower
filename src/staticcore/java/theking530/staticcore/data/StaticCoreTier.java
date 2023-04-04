package theking530.staticcore.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import theking530.staticcore.data.tier.cables.TierCableAttachmentConfiguration;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.data.tier.cables.TierHeatCableConfiguration;
import theking530.staticcore.data.tier.cables.TierItemCableConfiguration;
import theking530.staticcore.data.tier.cables.TierPowerCableConfiguration;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticcore.data.tier.categories.TierUpgradeConfiguration;

public abstract class StaticCoreTier {
	/****************
	 * Tier Settings
	 ****************/
	public final String modId;
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
	public final ConfigValue<Float> heatSinkOverheatTemperature;
	public final ConfigValue<Float> heatSinkMaximumTemperature;
	public final ConfigValue<Float> heatSinkConductivity;

	/***********************
	 * Machine Configuration
	 ***********************/
	public final ConfigValue<Float> defaultMachineOverheatTemperature;
	public final ConfigValue<Float> defaultMachineMaximumTemperature;
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

	public StaticCoreTier(ForgeConfigSpec.Builder builder, String modId) {
		this.modId = modId;

		tierId = builder.comment("The unique id of the tier in the format of 'MOD_ID:TIER_NAME'.")
				.translation(modId + ".config." + "tierId").define("TierId", getTierId().toString());
		unlocalizedTierName = builder.comment("The unlocalized name of the tier.")
				.translation(modId + ".config." + "unlocalizedTierName")
				.define("UnlocalizedTierName", getUnlocalizedName());

		builder.push("Power");
		powerConfiguration = createPowerConfiguration(builder, modId);
		builder.pop();

		builder.push("Upgrades");
		upgradeConfiguration = createUpgradeConfiguration(builder, modId);
		builder.pop();

		builder.push("Tools");
		toolConfiguration = createToolConfiguration(builder, modId);
		builder.pop();

		builder.push("Cables");
		{
			builder.push("Attachments");
			cableAttachmentConfiguration = createCableAttachmentConfiguration(builder, modId);
			builder.pop();

			builder.push("Fluid");
			cableFluidConfiguration = createFluidCableConfiguration(builder, modId);
			builder.pop();

			builder.push("Heat");
			cableHeatConfiguration = createHeatCableConfiguration(builder, modId);
			builder.pop();

			builder.push("Item");
			cableItemConfiguration = createItemCableConfiguration(builder, modId);
			builder.pop();

			builder.push("Power");
			cablePowerConfiguration = createPowerCableConfiguration(builder, modId);
			builder.pop();
		}
		builder.pop();

		builder.push("Digistore");
		digistoreCardCapacity = builder
				.comment("The number of items that can be contained in a regular digistore card of this tier.")
				.translation(modId + ".config." + "digistoreCardCapacity")
				.define("DigistoreCardCapacity", getDigistoreCardCapacity());
		builder.pop();

		builder.push("Machines");

		defaultMachineOverheatTemperature = builder.comment(
				"The temperature at which a machine of this tier overheats and stops processing (in mC [1C = 1000mC]).")
				.translation(modId + ".config." + "defaultMachineOverheatTemperature")
				.define("DefaultMachineOverheatTemperature", getDefaultMachineOverheatTemperature());
		defaultMachineMaximumTemperature = builder
				.comment("The maximum amount of heat a machine of this tier can contain (in mC [1C = 1000mC]).")
				.translation(modId + ".config." + "defaultMachineMaximumTemperature")
				.define("DefaultMachineMaximumTemperature", getDefaultMachineMaximumTemperature());
		defaultTankCapacity = builder
				.comment("The base amount of fluid a machine of this tier can store (in mB [1B = 1000mB]).")
				.translation(modId + ".config." + "defaultTankCapacity")
				.define("DefaultTankCapacity", this.getDefaultTankCapacity());

		builder.push("Conveyor");
		conveyorSpeedMultiplier = builder.comment("The speed multitplier applied to conveyors of this tier.")
				.translation(modId + ".config." + "conveyorSpeedMultiplier")
				.define("ConveyorSpeedMultiplier", getConveyorSpeedMultiplier());

		conveyorSupplierStackSize = builder
				.comment("The maximum stack size suppliers of this tier can consume at a time.")
				.translation(modId + ".config." + "conveyorSupplierStackSize")
				.define("ConveyorSupplierStackSize", getConveyorSupplierStackSize());

		conveyorExtractorStackSize = builder
				.comment(
						"The maximum stack size that an extractor of this tier can extract from an adjacent inventory.")
				.translation(modId + ".config." + "conveyorExtractorStackSize")
				.define("ConveyorExtractorStackSize", getConveyorExtractorStackSize());

		builder.pop();

		builder.push("Pump");
		pumpRate = builder.comment(
				"The amount of ticks that will elapse between each pump operation. The higher this number, the slower the pump will operate (20 ticks == 1 second).")
				.translation(modId + ".config." + "pumpRate").define("PumpRate", this.getPumpRate());
		builder.pop();

		builder.push("Turbine Blades");
		turbineBladeDurabilityTicks = builder
				.comment("The amount of ticks that a turbine blade will last (20 ticks == 1 second).")
				.translation(modId + ".config." + "turbineBladeDurabilityTicks")
				.define("TurbineBladeDurabilityTicks", this.getTurbineBladeDurabilityTicks());
		turbineBladeGenerationBoost = builder
				.comment("The multiplier applied to the generation of the turbine these blades are installed in..")
				.translation(modId + ".config." + "turbineBladeGenerationBoost")
				.define("TurbineBladeGenerationBoost", this.getTurbineBladeGenerationBoost());
		builder.pop();

		builder.push("Heatsink");
		heatSinkOverheatTemperature = builder
				.comment("The temperature above which this heatsink can no longer cool down other entities.")
				.translation(modId + ".config." + "heatSinkOverheatTemperature")
				.define("HeatSinkOverheatTemperature", this.getHeatsinkOverheatTemperature());

		heatSinkMaximumTemperature = builder.comment("The temperature above which this heatsink will be destroyed.")
				.translation(modId + ".config." + "heatSinkMaximumTemperature")
				.define("HeatSinkMaximumTemperature", this.getHeatsinkMaximumTemperature());

		heatSinkConductivity = builder.comment(
				"The conductivity multiplier for a heatsink of this tier. The higher it is, the faster it is able to dissipate heat.")
				.translation(modId + ".config." + "heatSinkConductivity")
				.define("HeatSinkConductivity", this.getHeatSinkConductivity());

		builder.pop();
		builder.pop();

		/********
		 * Items
		 ********/
		builder.push("Items");
		capsuleCapacity = builder.comment("The amount of fluid that can be stored in a fluid capsule of this tier.")
				.translation(modId + ".config." + "capsuleCapacity")
				.define("CapsuleCapacity", this.getCapsuleCapacity());
		itemFilterSlots = builder.comment(
				"The number of slots that exist on an item filter of this tier (not the filter attachment, the actual item).")
				.translation(modId + ".config." + "itemFilterSlots")
				.define("ItemFilterSlots", this.getItemFilterSlots());

		builder.pop();
	}

	public boolean hasUpgradeConfiguration() {
		return upgradeConfiguration != null;
	}

	protected abstract ResourceLocation getTierId();

	protected abstract String getUnlocalizedName();

	protected TierUpgradeConfiguration createUpgradeConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}

	protected TierPowerConfiguration createPowerConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}

	protected TierToolConfiguration createToolConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}

	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder,
			String modId) {
		return null;
	}

	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}

	protected TierHeatCableConfiguration createHeatCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}

	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}

	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return null;
	}

	protected int getDefaultTankCapacity() {
		return 0;
	}

	protected int getCapsuleCapacity() {
		return 0;
	}

	protected float getDefaultMachineOverheatTemperature() {
		return 0;
	}

	protected float getDefaultMachineMaximumTemperature() {
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

	protected float getHeatsinkOverheatTemperature() {
		return 0;
	}

	protected float getHeatsinkMaximumTemperature() {
		return 0;
	}

	protected float getHeatSinkConductivity() {
		return 0;
	}

	protected int getItemFilterSlots() {
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
