package theking530.staticpower.data.tiers.lumum;

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

public class StaticCoreTierLumum extends StaticCoreTier {

	public StaticCoreTierLumum(Builder builder, String modId) {
		super(builder, modId);
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
	protected int getDigistoreCardCapacity() {
		return 262144;
	}

	@Override
	protected int getItemFilterSlots() {
		return 18;
	}

	@Override
	protected float getDefaultMachineOverheatTemperature() {
		return 2800.0f;
	}

	@Override
	protected float getDefaultMachineMaximumTemperature() {
		return 4000.0f;
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
	protected TierPowerConfiguration createPowerConfiguration(Builder builder, String modId) {
		return new LumumPowerConfiguration(builder, modId);
	}

	@Override
	protected TierCableAttachmentConfiguration createCableAttachmentConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new LumumCableConfiguration.CableAttachmentConfiguration(builder, modId);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new LumumCableConfiguration.FluidCableConfiguration(builder, modId);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new LumumCableConfiguration.ItemCableConfiguration(builder, modId);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new LumumCableConfiguration.PowerCableConfiguration(builder, modId);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder, String modId) {
		return new LumumUpgradeConfiguration(builder, modId);
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new LumumToolConfiguration(builder, modId);
	}
}
