package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierSapphire extends StaticPowerTier {

	public StaticPowerTierSapphire(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "sapphire");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.sapphire";
	}
	protected Double getHardenedDurabilityBoost() {
		return 1.5;
	}

	protected Boolean isHardenedDurabilityBoostAdditive() {
		return false;
	}
}
