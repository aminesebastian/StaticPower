package theking530.staticpower.data.tiers.advanced;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.cables.TierCableAttachmentConfiguration;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.data.tier.cables.TierItemCableConfiguration;
import theking530.staticcore.data.tier.cables.TierPowerCableConfiguration;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticcore.data.tier.categories.TierUpgradeConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierAdvanced extends StaticCoreTier {

	public StaticCoreTierAdvanced(Builder builder, String modId) {
		super(builder, modId);
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
	protected float getDefaultMachineThermalConductivity() {
		return 10;
	}

	@Override
	protected float getDefaultMachineThermalMass() {
		return 100;
	}

	@Override
	protected float getDefaultMachineOverheatTemperature() {
		return 350.0f;
	}

	@Override
	protected float getDefaultMachineMaximumTemperature() {
		return 500.0f;
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
	protected TierPowerConfiguration createPowerConfiguration(Builder builder, String modId) {
		return new AdvancedPowerConfiguration(builder, modId);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder,
			String modId) {
		return new AdvancedCableConfiguration.CableAttachmentConfiguration(builder, modId);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new AdvancedCableConfiguration.FluidCableConfiguration(builder, modId);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new AdvancedCableConfiguration.ItemCableConfiguration(builder, modId);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new AdvancedCableConfiguration.PowerCableConfiguration(builder, modId);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder, String modId) {
		return null;
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new AdvancedToolConfiguration(builder, modId);
	}
}
