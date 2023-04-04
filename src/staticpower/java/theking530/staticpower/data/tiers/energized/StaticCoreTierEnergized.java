package theking530.staticpower.data.tiers.energized;

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

public class StaticCoreTierEnergized extends StaticCoreTier {

	public StaticCoreTierEnergized(Builder builder, String modId) {
		super(builder, modId);
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
	protected float getDefaultMachineOverheatTemperature() {
		return 1400.0f;
	}

	@Override
	protected float getDefaultMachineMaximumTemperature() {
		return 2000.0f;
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
	protected TierPowerConfiguration createPowerConfiguration(Builder builder, String modId) {
		return new EnergizedPowerConfiguration(builder, modId);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new EnergizedCableConfiguration.CableAttachmentConfiguration(builder, modId);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new EnergizedCableConfiguration.FluidCableConfiguration(builder, modId);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new EnergizedCableConfiguration.ItemCableConfiguration(builder, modId);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new EnergizedCableConfiguration.PowerCableConfiguration(builder, modId);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder, String modId) {
		return new EnergizedUpgradeConfiguration(builder, modId);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new EnergizedToolConfiguration(builder, modId);
	}
}
