package theking530.staticpower.data.tiers.creative;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.tiers.categories.TierPowerConfiguration;
import theking530.staticpower.data.tiers.categories.TierToolConfiguration;
import theking530.staticpower.data.tiers.categories.TierUpgradeConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class StaticPowerTierCreative extends StaticPowerTier {

	public StaticPowerTierCreative(Builder builder) {
		super(builder);
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
	protected TierPowerConfiguration createPowerConfiguration(Builder builder) {
		return new CreativePowerConfiguration(builder);
	}

	@Override
	protected TierFluidCableConfiguration createFluidCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new CreativeCableConfiguration.FluidCableConfiguration(builder);
	}

	@Override
	protected TierItemCableConfiguration createItemCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new CreativeCableConfiguration.ItemCableConfiguration(builder);
	}

	@Override
	protected TierPowerCableConfiguration createPowerCableConfiguration(ForgeConfigSpec.Builder builder) {
		return new CreativeCableConfiguration.PowerCableConfiguration(builder);
	}

	@Override
	protected TierUpgradeConfiguration createUpgradeConfiguration(Builder builder) {
		return null;
	}

	@Override
	protected TierToolConfiguration createToolConfiguration(Builder builder) {
		return new CreativeToolConfiguration(builder);
	}
}
