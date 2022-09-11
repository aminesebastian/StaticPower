package theking530.staticpower.data.tiers.basic;

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

public class StaticPowerTierBasic extends StaticPowerTier {

	public StaticPowerTierBasic(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "basic");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.basic";
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 1000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(0.0, 5.0);
	}

	@Override
	protected double getPortableBatteryMaxOutputCurrent() {
		return 1;
	}

	@Override
	protected double getPortableBatteryOutputVoltage() {
		return 5;
	}

	@Override
	protected int getPumpRate() {
		return 100;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 4096;
	}

	@Override
	protected int getItemFilterSlots() {
		return 3;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(125);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(250);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 5000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 4000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 1.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 108000;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 1;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 4;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 4;
	}

	@Override
	protected TierPowerConfiguration createPowerConfiguration(Builder builder) {
		return new BasicPowerConfiguration(builder);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder) {
		return new BasicCableConfiguration.CableAttachmentConfiguration(builder);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new BasicCableConfiguration.FluidCableConfiguration(builder);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new BasicCableConfiguration.ItemCableConfiguration(builder);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new BasicCableConfiguration.PowerCableConfiguration(builder);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder) {
		return new BasicUpgradeConfiguration(builder);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new BasicToolConfiguration(builder);
	}
}
