package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierBronze extends StaticPowerTier {

	public StaticPowerTierBronze(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "bronze");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.bronze";
	}

	@Override
	protected int getDrillBitUses() {
		return 2000;
	}
}
