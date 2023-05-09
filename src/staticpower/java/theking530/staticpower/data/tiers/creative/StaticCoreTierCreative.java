package theking530.staticpower.data.tiers.creative;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.data.tier.cables.TierItemCableConfiguration;
import theking530.staticcore.data.tier.cables.TierPowerCableConfiguration;
import theking530.staticcore.data.tier.categories.TierPowerConfiguration;
import theking530.staticcore.data.tier.categories.TierToolConfiguration;
import theking530.staticcore.data.tier.categories.TierUpgradeConfiguration;
import theking530.staticpower.StaticPower;

public class StaticCoreTierCreative extends StaticCoreTier {

	public StaticCoreTierCreative(Builder builder, String modId) {
		super(builder, modId);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "creative");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.creative";
	}

	@Override
	protected int getPumpRate() {
		return 10;
	}

	@Override
	protected int getDigistoreCardCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected int getCapsuleCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 5.0;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected float getDefaultMachineThermalConductivity() {
		return 1000;
	}

	@Override
	protected float getDefaultMachineThermalMass() {
		return 1;
	}

	@Override
	protected float getDefaultMachineOverheatTemperature() {
		return Integer.MAX_VALUE / 2;
	}

	@Override
	protected float getDefaultMachineMaximumTemperature() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected TierPowerConfiguration createPowerConfiguration(Builder builder, String modId) {
		return new CreativePowerConfiguration(builder, modId);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new CreativeCableConfiguration.FluidCableConfiguration(builder, modId);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new CreativeCableConfiguration.ItemCableConfiguration(builder, modId);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder, String modId) {
		return new CreativeCableConfiguration.PowerCableConfiguration(builder, modId);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder, String modId) {
		return null;
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder, String modId) {
		return new CreativeToolConfiguration(builder, modId);
	}
}
