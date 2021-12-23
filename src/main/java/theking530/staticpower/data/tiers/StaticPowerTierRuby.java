package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierRuby extends StaticPowerTier {

	public StaticPowerTierRuby(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "ruby");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.ruby";
	}

	protected Double getHardenedDurabilityBoost() {
		return 5000.0;
	}

	protected Boolean isHardenedDurabilityBoostAdditive() {
		return true;
	}
}
