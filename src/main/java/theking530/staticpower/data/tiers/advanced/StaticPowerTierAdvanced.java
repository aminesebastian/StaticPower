package theking530.staticpower.data.tiers.advanced;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;
import theking530.staticpower.data.tiers.categories.TierUpgradeConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierCableAttachmentConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class StaticPowerTierAdvanced extends StaticPowerTier {

	public StaticPowerTierAdvanced(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "advanced");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.advanced";
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 5000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(5.0, 12.0);
	}

	@Override
	protected double getPortableBatteryMaxOutputCurrent() {
		return 2;
	}

	@Override
	protected double getPortableBatteryOutputVoltage() {
		return 12;
	}

	@Override
	protected int getPumpRate() {
		return 80;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 16384;
	}

	@Override
	protected int getItemFilterSlots() {
		return 5;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(350);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(500);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 7500;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 8000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 1.5;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 216000;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 1.5;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 8;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 8;
	}

	@Override
	protected TierPowerConfiguration createPowerConfiguration(Builder builder) {
		return new AdvancedPowerConfiguration(builder);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder) {
		return new AdvancedCableConfiguration.CableAttachmentConfiguration(builder);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new AdvancedCableConfiguration.FluidCableConfiguration(builder);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new AdvancedCableConfiguration.ItemCableConfiguration(builder);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new AdvancedCableConfiguration.PowerCableConfiguration(builder);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder) {
		return null;
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new AdvancedToolConfiguration(builder);
	}
}
