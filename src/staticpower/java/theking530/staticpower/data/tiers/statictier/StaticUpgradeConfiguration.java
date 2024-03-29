package theking530.staticpower.data.tiers.statictier;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.data.tier.categories.TierUpgradeConfiguration;
import theking530.staticpower.StaticPower;

public class StaticUpgradeConfiguration extends TierUpgradeConfiguration {

	public StaticUpgradeConfiguration(Builder builder, String modId) {
		super(builder, StaticPower.MOD_ID);
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 3;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 6.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 4.0f;
	}

	@Override
	public double getHeatCapacityUpgrade() {
		return 2.0f;
	}

	@Override
	public double getHeatConductivityUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 4f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 6f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.75f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1000;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 4.0f;
	}
}
