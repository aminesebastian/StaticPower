package theking530.staticpower.data.tiers.lumum;

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

public class StaticPowerTierLumum extends StaticPowerTier {

	public StaticPowerTierLumum(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "lumum");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.lumum";
	}

	@Override
	protected int getPumpRate() {
		return 20;
	}

	@Override
	protected int getItemFilterSlots() {
		return 18;
	}

	@Override
	protected int getDefaultMachineOverheatTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(2800);
	}

	@Override
	protected int getDefaultMachineMaximumTemperature() {
		return CapabilityHeatable.convertHeatToMilliHeat(4000);
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 20000;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 64000;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 3.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 1728000;
	}

	@Override
	protected double getConveyorSpeedMultiplier() {
		return 4;
	}

	@Override
	protected int getConveyorSupplierStackSize() {
		return 64;
	}

	@Override
	protected int getConveyorExtractorStackSize() {
		return 64;
	}

	@Override
	protected TierPowerConfiguration createPowerConfiguration(Builder builder) {
		return new LumumPowerConfiguration(builder);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder) {
		return new LumumCableConfiguration.CableAttachmentConfiguration(builder);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new LumumCableConfiguration.FluidCableConfiguration(builder);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new LumumCableConfiguration.ItemCableConfiguration(builder);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new LumumCableConfiguration.PowerCableConfiguration(builder);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder) {
		return new LumumUpgradeConfiguration(builder);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new LumumToolConfiguration(builder);
	}
}
