package theking530.staticcore.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.tier.categories.TierUpgradeConfiguration;

public class BasicUpgradeConfiguration extends TierUpgradeConfiguration {

	public BasicUpgradeConfiguration(Builder builder, String modId) {
		super(builder, StaticCore.MOD_ID);
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 1;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 750;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 2.0f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 1.25f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 1.25f;
	}

	@Override
	public double getHeatCapacityUpgrade() {
		return 1f;
	}

	@Override
	public double getHeatConductivityUpgrade() {
		return 1f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 2f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 2f;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 2.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 3.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 1.5f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 2.0f;
	}
}
