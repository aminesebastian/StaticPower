package theking530.staticpower.data.tiers.basic;

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

public class StaticCoreTierBasic extends StaticCoreTier {

	public StaticCoreTierBasic(Builder builder, String modId) {
		super(builder, modId);
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
	protected float getDefaultMachineThermalConductivity() {
		return 100;
	}

	@Override
	protected float getDefaultMachineThermalMass() {
		return 100;
	}

	@Override
	protected float getDefaultMachineOverheatTemperature() {
		return 125.0f;
	}

	@Override
	protected float getDefaultMachineMaximumTemperature() {
		return 250.0f;
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
	protected TierPowerConfiguration createPowerConfiguration(Builder builder, String modId) {
		return new BasicPowerConfiguration(builder, modId);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder,
			String modId) {
		return new BasicCableConfiguration.CableAttachmentConfiguration(builder, modId);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new BasicCableConfiguration.FluidCableConfiguration(builder, modId);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new BasicCableConfiguration.ItemCableConfiguration(builder, modId);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new BasicCableConfiguration.PowerCableConfiguration(builder, modId);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder, String modId) {
		return new BasicUpgradeConfiguration(builder, modId);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new BasicToolConfiguration(builder, modId);
	}
}
