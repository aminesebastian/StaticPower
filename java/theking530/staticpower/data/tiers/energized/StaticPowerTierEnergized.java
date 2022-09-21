package theking530.staticpower.data.tiers.energized;

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

public class StaticPowerTierEnergized extends StaticPowerTier {

	public StaticPowerTierEnergized(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "energized");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.energized";
	}

	@Override
	protected int getPumpRate() {
		return 40;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return 131072;
	}

	@Override
	protected int getItemFilterSlots() {
		return 9;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(1400);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(2000);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 15000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 32000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 2.5;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 864000;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 3;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 32;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 32;
	}

	@Override
	protected TierPowerConfiguration createPowerConfiguration(Builder builder) {
		return new EnergizedPowerConfiguration(builder);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder) {
		return new EnergizedCableConfiguration.CableAttachmentConfiguration(builder);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new EnergizedCableConfiguration.FluidCableConfiguration(builder);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new EnergizedCableConfiguration.ItemCableConfiguration(builder);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new EnergizedCableConfiguration.PowerCableConfiguration(builder);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder) {
		return new EnergizedUpgradeConfiguration(builder);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new EnergizedToolConfiguration(builder);
	}
}
