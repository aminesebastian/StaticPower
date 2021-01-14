package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierZinc extends StaticPowerTier {

	public StaticPowerTierZinc(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "zinc");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.zinc";
	}

	@Override
	protected int getHammerUses() {
		return 300;
	}

	@Override
	protected int getWireCutterUses() {
		return 300;
	}
}
