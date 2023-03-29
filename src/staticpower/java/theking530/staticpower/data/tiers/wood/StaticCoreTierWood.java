package theking530.staticpower.data.tiers.wood;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticpower.StaticPower;

public class StaticCoreTierWood extends StaticCoreTier {

	public StaticCoreTierWood(Builder builder, String modId) {
		super(builder, modId);
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
