package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierDiamond extends StaticPowerTier {

	public StaticPowerTierDiamond(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "diamond");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.diamond";
	}
	protected Double getHardenedDurabilityBoost() {
		return 2.0;
	}
	
	protected Boolean isHardenedDurabilityBoostAdditive() {
			return false;
	}
}
