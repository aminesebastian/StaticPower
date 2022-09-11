package theking530.staticpower.data.tiers.energized;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.TierUpgradeConfiguration;

public class EnergizedUpgradeConfiguration extends TierUpgradeConfiguration {

	public EnergizedUpgradeConfiguration(Builder builder) {
		super(builder);
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 4;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 8.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 10.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 8.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 5.0f;
	}

	@Override
	public float getHeatCapacityUpgrade() {
		return 4.0f;
	}

	@Override
	public float getHeatConductivityUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 6.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 10f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 3.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 2.0f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1250;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 5.0f;
	}
}
