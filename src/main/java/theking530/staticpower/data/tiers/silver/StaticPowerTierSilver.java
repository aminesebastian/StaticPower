package theking530.staticpower.data.tiers.silver;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierSilver extends StaticPowerTier {

	public StaticPowerTierSilver(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "silver");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.silver";
	}
}
