package theking530.staticpower.data.tiers.statictier;

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

public class StaticPowerTierStatic extends StaticPowerTier {

	public StaticPowerTierStatic(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "static");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.static";
	}

	@Override
	protected double getPortableBatteryCapacity() {
		return 10000;
	}

	@Override
	protected List<Double> internalGetPortableBatteryChargingVoltage() {
		return Arrays.asList(12.0, 24.0);
	}

	@Override
	protected double getPortableBatteryMaxOutputCurrent() {
		return 3;
	}

	@Override
	protected double getPortableBatteryOutputVoltage() {
		return 24;
	}

	@Override
	protected int getPumpRate() {
		return 60;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 65536;
	}

	@Override
	protected int getItemFilterSlots() {
		return 7;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(700);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(1000);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 10000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 16000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 2;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 432000;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 2;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 16;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 16;
	}

	@Override
	protected TierPowerConfiguration createPowerConfiguration(Builder builder) {
		return new StaticPowerConfiguration(builder);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder) {
		return new StaticCableConfiguration.CableAttachmentConfiguration(builder);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new StaticCableConfiguration.FluidCableConfiguration(builder);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new StaticCableConfiguration.ItemCableConfiguration(builder);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new StaticCableConfiguration.PowerCableConfiguration(builder);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder) {
		return new StaticUpgradeConfiguration(builder);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new StaticToolConfiguration(builder);
	}
}
