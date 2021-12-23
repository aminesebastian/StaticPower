package theking530.staticpower.data.tiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierEmerald extends StaticPowerTier {

	public StaticPowerTierEmerald(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "diamond");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.emerald";
	}

	protected Double getHardenedDurabilityBoost() {
		return 10000.0;
	}

	protected Boolean isHardenedDurabilityBoostAdditive() {
		return true;
	}
}
