package theking530.staticpower.data.tiers.statictier;

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

public class StaticCoreTierStatic extends StaticCoreTier {

	public StaticCoreTierStatic(Builder builder, String modId) {
		super(builder, modId);
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
	protected float getDefaultMachineThermalConductivity() {
		return 100;
	}

	@Override
	protected float getDefaultMachineThermalMass() {
		return 100;
	}

	@Override
	protected float getDefaultMachineOverheatTemperature() {
		return 700.0f;
	}

	@Override
	protected float getDefaultMachineMaximumTemperature() {
		return 1000.0f;
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
	protected TierPowerConfiguration createPowerConfiguration(Builder builder, String modId) {
		return new StaticPowerConfiguration(builder, modId);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder,
			String modId) {
		return new StaticCableConfiguration.CableAttachmentConfiguration(builder, modId);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new StaticCableConfiguration.FluidCableConfiguration(builder, modId);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new StaticCableConfiguration.ItemCableConfiguration(builder, modId);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new StaticCableConfiguration.PowerCableConfiguration(builder, modId);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder, String modId) {
		return new StaticUpgradeConfiguration(builder, modId);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new StaticToolConfiguration(builder, modId);
	}
}
