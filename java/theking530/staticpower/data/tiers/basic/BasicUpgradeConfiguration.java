package theking530.staticpower.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierUpgradeConfiguration;

public class BasicUpgradeConfiguration extends TierUpgradeConfiguration {

	public BasicUpgradeConfiguration(Builder builder) {
		super(builder);
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
	public float getHeatCapacityUpgrade() {
		return 1f;
	}

	@Override
	public float getHeatConductivityUpgrade() {
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
