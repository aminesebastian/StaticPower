package theking530.staticpower.data.tiers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTier;

public class StaticPowerTierWood extends StaticPowerTier {

	public StaticPowerTierWood(Builder builder) {
		super(builder);
	}

	@Override
	protected ResourceLocation getTierId() {
		return new ResourceLocation(StaticPower.MOD_ID, "wood");
	}

	@Override
	protected String getUnlocalizedName() {
		return "tier.staticpower.wood";
	}

	@Override
	protected double getTurbineBladeGenerationBoost() {
		return 0.5;
	}

	@Override
	protected int getTurbineBladeDurabilityTicks() {
		return 54000;
	}
}
