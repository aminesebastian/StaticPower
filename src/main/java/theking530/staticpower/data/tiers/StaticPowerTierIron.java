package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierIron extends StaticPowerTier {

	public StaticPowerTierIron(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "iron");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.iron";
	}

	@Override
	protected int getDrillBitUses() {
		return 2000;
	}
}
