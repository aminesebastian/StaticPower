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

	@Override
	protected int getChainsawBladeUses() {
		return 4000;
	}

	@Override
	protected int getPumpRate() {
		return 120;
	}

	@Override
	protected int getCapsuleCapacity() {
		return 2000;
	}

	@Override
	protected int getDefaultTankCapacity() {
		return 2500;
	}

	@Override
	protected long getDefaultMachinePowerCapacity() {
		return 200000;
	}

	@Override
	protected long getDefaultMachinePowerInput() {
		return 5000;
	}

	@Override
	protected long getDefaultMachinePowerOutput() {
		return 5000;
	}

	@Override
	protected int getHammerUses() {
		return 250;
	}

	@Override
	protected int getWireCutterUses() {
		return 500;
	}
}
